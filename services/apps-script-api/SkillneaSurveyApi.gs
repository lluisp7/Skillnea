const SHEET_TESTS = 'tests';
const SHEET_QUESTIONS = 'questions';
const SHEET_RESPONSES = 'responses';

function doGet(e) {
  const action = (e.parameter.action || '').trim();

  if (action === 'tests') {
    return jsonResponse({
      status: 'ok',
      data: getTests_(),
    });
  }

  if (action === 'questions') {
    const testId = (e.parameter.testId || '').trim();
    return jsonResponse({
      status: 'ok',
      data: getQuestions_(testId),
    });
  }

  return jsonResponse({
    status: 'error',
    message: 'Unsupported action',
  });
}

function doPost(e) {
  const action = (e.parameter.action || '').trim();

  if (action !== 'submit') {
    return jsonResponse({
      status: 'error',
      message: 'Unsupported action',
    });
  }

  const payload = JSON.parse(e.postData.contents || '{}');
  const responseSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(SHEET_RESPONSES);

  if (!responseSheet) {
    return jsonResponse({
      status: 'error',
      message: 'responses sheet not found',
    });
  }

  const submissionId = Utilities.getUuid();
  const submittedAt = payload.submittedAt || new Date().toISOString();
  const answers = payload.answers || [];

  answers.forEach(function(answer) {
    responseSheet.appendRow([
      submissionId,
      payload.participantEmail || '',
      payload.testId || '',
      submittedAt,
      answer.questionId || '',
      answer.optionId || '',
      answer.score || 0,
    ]);
  });

  return jsonResponse({
    status: 'ok',
    message: 'Submission stored',
    submissionId: submissionId,
  });
}

function getTests_() {
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(SHEET_TESTS);
  if (!sheet) {
    return [];
  }

  return recordsFromSheet_(sheet).map(function(row) {
    return {
      id: row.id,
      title: row.title,
      description: row.description,
      estimatedMinutes: toNumber_(row.estimatedMinutes),
      category: row.category,
      questionCount: toNumber_(row.questionCount),
      active: String(row.active).toLowerCase() === 'true',
    };
  }).filter(function(test) {
    return test.active;
  });
}

function getQuestions_(testId) {
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(SHEET_QUESTIONS);
  if (!sheet) {
    return [];
  }

  const grouped = {};

  recordsFromSheet_(sheet)
    .filter(function(row) {
      return row.testId === testId;
    })
    .forEach(function(row) {
      const questionId = row.id;

      if (!grouped[questionId]) {
        grouped[questionId] = {
          id: row.id,
          testId: row.testId,
          order: toNumber_(row.order),
          prompt: row.prompt,
          helper: row.helper,
          dimension: row.dimension,
          options: [],
        };
      }

      grouped[questionId].options.push({
        id: row.optionId,
        label: row.optionLabel,
        value: toNumber_(row.optionValue),
      });
    });

  return Object.keys(grouped)
    .map(function(key) {
      return grouped[key];
    })
    .sort(function(a, b) {
      return a.order - b.order;
    });
}

function recordsFromSheet_(sheet) {
  const values = sheet.getDataRange().getValues();
  if (values.length < 2) {
    return [];
  }

  const headers = values[0];

  return values.slice(1).map(function(row) {
    const record = {};
    headers.forEach(function(header, index) {
      record[String(header).trim()] = row[index];
    });
    return record;
  });
}

function jsonResponse(payload) {
  return ContentService
    .createTextOutput(JSON.stringify(payload))
    .setMimeType(ContentService.MimeType.JSON);
}

function toNumber_(value) {
  return Number(value || 0);
}
