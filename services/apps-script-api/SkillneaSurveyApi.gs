const SPREADSHEET_ID = '1zcipqBrxbdc44vazykQaVznRIFTrSswMhCoxcpJ-H0U';
const DEFAULT_TEST_ID = 'critical-thinking-rubric';

const SHEET_NAMES = {
  responses: ['responses', 'respuestas', 'results', 'submissions'],
};

const HEADER_ALIASES = {
  id: ['id'],
  disposition: ['disposition', 'disposicion'],
  level: ['level', 'nivel'],
  rubric: ['rubric', 'rubrica'],
  question: ['question', 'pregunta', 'prompt', 'enunciado'],
};

function doGet(e) {
  const action = getParam_(e, 'action');

  if (action === 'health') {
    return jsonResponse_({
      status: 'ok',
      message: 'Skillnea Apps Script API is running',
      spreadsheetId: SPREADSHEET_ID,
    });
  }

  if (action === 'schema') {
    return jsonResponse_({
      status: 'ok',
      data: inspectSchema_(),
    });
  }

  if (action === 'tests') {
    return jsonResponse_({
      status: 'ok',
      data: getTests_(),
    });
  }

  if (action === 'dispositions') {
    return jsonResponse_({
      status: 'ok',
      data: getDispositions_(),
    });
  }

  if (action === 'questions') {
    return jsonResponse_({
      status: 'ok',
      data: getQuestions_({
        testId: getParam_(e, 'testId'),
        disposition: getParam_(e, 'disposition'),
      }),
    });
  }

  return jsonResponse_({
    status: 'error',
    message: 'Unsupported action',
  });
}

function doPost(e) {
  const action = getParam_(e, 'action');
  if (action !== 'submit') {
    return jsonResponse_({
      status: 'error',
      message: 'Unsupported action',
    });
  }

  const payload = JSON.parse((e && e.postData && e.postData.contents) || '{}');
  const answers = normalizeAnswers_(payload);

  if (!answers.length) {
    return jsonResponse_({
      status: 'error',
      message: 'No answers received',
    });
  }

  const sheet = ensureResponsesSheet_();
  const submissionId = Utilities.getUuid();
  const submittedAt = payload.submittedAt || new Date().toISOString();
  const participantEmail = payload.participantEmail || payload.email || '';
  const participantName = payload.participantName || payload.name || '';
  const testId = payload.testId || DEFAULT_TEST_ID;

  answers.forEach(function(answer) {
    sheet.appendRow([
      submissionId,
      participantEmail,
      participantName,
      testId,
      submittedAt,
      answer.questionId || '',
      answer.disposition || '',
      toNumber_(answer.rubricLevel || answer.level),
      toNumber_(answer.score),
      answer.answerText || '',
      answer.optionId || '',
    ]);
  });

  return jsonResponse_({
    status: 'ok',
    message: 'Submission stored',
    submissionId: submissionId,
    storedAnswers: answers.length,
  });
}

function getTests_() {
  const questionRows = getQuestionRows_();
  const dispositions = uniqueValues_(questionRows.map(function(row) {
    return row.disposition;
  }));

  return [{
    id: DEFAULT_TEST_ID,
    title: 'Critical Thinking Assessment',
    description: 'Banco de preguntas rubricadas por disposition y level desde Google Sheets.',
    estimatedMinutes: Math.max(10, questionRows.length * 2),
    category: 'HR Behavior',
    questionCount: questionRows.length,
    dispositionCount: dispositions.length,
    active: true,
    responseType: 'text',
  }];
}

function getDispositions_() {
  return uniqueValues_(getQuestionRows_().map(function(row) {
    return row.disposition;
  })).map(function(disposition) {
    return {
      id: slugify_(disposition),
      label: disposition,
    };
  });
}

function getQuestions_(filters) {
  const testId = filters.testId || DEFAULT_TEST_ID;
  const dispositionFilter = normalizeKey_(filters.disposition);

  return getQuestionRows_()
    .filter(function(row) {
      if (testId && testId !== DEFAULT_TEST_ID) {
        return false;
      }
      if (!dispositionFilter) {
        return true;
      }
      return normalizeKey_(row.disposition) === dispositionFilter;
    })
    .map(function(row, index) {
      return {
        id: row.id,
        testId: DEFAULT_TEST_ID,
        order: index + 1,
        prompt: row.question,
        helper: 'Respuesta abierta. La evaluación usa la rúbrica asociada a esta pregunta.',
        dimension: row.disposition,
        disposition: row.disposition,
        responseType: 'text',
        rubricLevel: row.level,
        rubric: row.rubric,
        options: [],
      };
    });
}

function getQuestionRows_() {
  const sheet = findQuestionSheet_();
  if (!sheet) {
    return [];
  }

  return rowsAsObjects_(sheet)
    .map(function(row, index) {
      return {
        id: String(readField_(row, HEADER_ALIASES.id, index + 1)),
        disposition: String(readField_(row, HEADER_ALIASES.disposition, 'General')),
        level: toNumber_(readField_(row, HEADER_ALIASES.level, 0)),
        rubric: String(readField_(row, HEADER_ALIASES.rubric, '')),
        question: String(readField_(row, HEADER_ALIASES.question, '')),
      };
    })
    .filter(function(row) {
      return row.id && row.question;
    });
}

function inspectSchema_() {
  const spreadsheet = openSpreadsheet_();
  return spreadsheet.getSheets().map(function(sheet) {
    const values = sheet.getDataRange().getValues();
    const headers = values.length ? values[0] : [];
    return {
      name: sheet.getName(),
      rowCount: values.length,
      headers: headers,
    };
  });
}

function ensureResponsesSheet_() {
  const spreadsheet = openSpreadsheet_();
  const existing = findSheetByAliases_(SHEET_NAMES.responses);
  if (existing) {
    ensureResponsesHeader_(existing);
    return existing;
  }

  const sheet = spreadsheet.insertSheet('responses');
  ensureResponsesHeader_(sheet);
  return sheet;
}

function ensureResponsesHeader_(sheet) {
  const values = sheet.getDataRange().getValues();
  if (!values.length) {
    sheet.appendRow([
      'submissionId',
      'participantEmail',
      'participantName',
      'testId',
      'submittedAt',
      'questionId',
      'disposition',
      'rubricLevel',
      'score',
      'answerText',
      'optionId',
    ]);
  }
}

function normalizeAnswers_(payload) {
  const rawAnswers = Array.isArray(payload.answers)
    ? payload.answers
    : Array.isArray(payload.responses)
      ? payload.responses
      : [];

  return rawAnswers.map(function(answer) {
    return {
      questionId: answer.questionId || answer.id || '',
      disposition: answer.disposition || answer.dimension || '',
      rubricLevel: answer.rubricLevel || answer.level || '',
      score: answer.score || '',
      answerText: answer.answerText || answer.text || answer.response || '',
      optionId: answer.optionId || '',
    };
  });
}

function findQuestionSheet_() {
  const spreadsheet = openSpreadsheet_();
  const sheets = spreadsheet.getSheets();

  for (var i = 0; i < sheets.length; i += 1) {
    var sheet = sheets[i];
    var values = sheet.getDataRange().getValues();
    if (!values.length) {
      continue;
    }

    var normalizedHeaders = values[0].map(normalizeKey_);
    var hasId = normalizedHeaders.indexOf(normalizeKey_(HEADER_ALIASES.id[0])) !== -1;
    var hasDisposition = containsAlias_(normalizedHeaders, HEADER_ALIASES.disposition);
    var hasLevel = containsAlias_(normalizedHeaders, HEADER_ALIASES.level);
    var hasRubric = containsAlias_(normalizedHeaders, HEADER_ALIASES.rubric);
    var hasQuestion = containsAlias_(normalizedHeaders, HEADER_ALIASES.question);

    if (hasId && hasDisposition && hasLevel && hasRubric && hasQuestion) {
      return sheet;
    }
  }

  return null;
}

function rowsAsObjects_(sheet) {
  const values = sheet.getDataRange().getValues();
  if (values.length < 2) {
    return [];
  }

  const headers = values[0].map(function(header) {
    return normalizeKey_(header);
  });

  return values.slice(1).map(function(row) {
    const record = {};
    headers.forEach(function(header, index) {
      record[header] = row[index];
    });
    return record;
  });
}

function findSheetByAliases_(aliases) {
  const spreadsheet = openSpreadsheet_();
  const sheets = spreadsheet.getSheets();
  const normalizedAliases = aliases.map(normalizeKey_);

  for (var i = 0; i < sheets.length; i += 1) {
    const sheet = sheets[i];
    const normalizedName = normalizeKey_(sheet.getName());
    if (normalizedAliases.indexOf(normalizedName) !== -1) {
      return sheet;
    }
  }

  return null;
}

function readField_(row, aliases, fallback) {
  for (var i = 0; i < aliases.length; i += 1) {
    var key = normalizeKey_(aliases[i]);
    if (row[key] !== undefined && row[key] !== null && row[key] !== '') {
      return row[key];
    }
  }
  return fallback;
}

function containsAlias_(normalizedHeaders, aliases) {
  return aliases.some(function(alias) {
    return normalizedHeaders.indexOf(normalizeKey_(alias)) !== -1;
  });
}

function normalizeKey_(value) {
  return String(value || '')
    .toLowerCase()
    .trim()
    .replace(/[áàäâ]/g, 'a')
    .replace(/[éèëê]/g, 'e')
    .replace(/[íìïî]/g, 'i')
    .replace(/[óòöô]/g, 'o')
    .replace(/[úùüû]/g, 'u')
    .replace(/ñ/g, 'n')
    .replace(/[^a-z0-9]/g, '');
}

function slugify_(value) {
  return String(value || '')
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');
}

function uniqueValues_(values) {
  const result = [];
  const seen = {};

  values.forEach(function(value) {
    var safeValue = String(value || '').trim();
    if (!safeValue || seen[safeValue]) {
      return;
    }
    seen[safeValue] = true;
    result.push(safeValue);
  });

  return result;
}

function getParam_(e, key) {
  return String((e && e.parameter && e.parameter[key]) || '').trim();
}

function openSpreadsheet_() {
  return SpreadsheetApp.openById(SPREADSHEET_ID);
}

function jsonResponse_(payload) {
  return ContentService
    .createTextOutput(JSON.stringify(payload))
    .setMimeType(ContentService.MimeType.JSON);
}

function toNumber_(value) {
  var numeric = Number(value);
  return isNaN(numeric) ? 0 : numeric;
}
