const http = require('http');
const fs = require('fs');
const path = require('path');

loadEnvFile();

const port = process.env.PORT || 8080;
const appsScriptBaseUrl = process.env.APPS_SCRIPT_BASE_URL || '';
const appsScriptDeploymentId = process.env.APPS_SCRIPT_DEPLOYMENT_ID || '';
const serviceVersion = process.env.SERVICE_VERSION || '0.1.0';

const sendJson = (res, statusCode, payload) => {
  response(res, statusCode, payload);
};

const response = (res, statusCode, payload) => {
  res.writeHead(statusCode, {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Content-Type': 'application/json',
  });
  res.end(JSON.stringify(payload));
};

const server = http.createServer(async (req, res) => {
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET,OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    });
    res.end();
    return;
  }

  const requestUrl = new URL(req.url, `http://localhost:${port}`);

  if (req.url === '/health') {
    response(res, 200, {
      status: 'ok',
      service: 'skillnea-admin-api',
      version: serviceVersion,
    });
    return;
  }

  if (requestUrl.pathname === '/config') {
    response(res, 200, {
      status: 'ok',
      apiMode: appsScriptBaseUrl ? 'apps-script' : 'demo',
      appsScriptConfigured: Boolean(appsScriptBaseUrl),
      appsScriptBaseUrl,
      appsScriptDeploymentId,
      versions: {
        adminApi: serviceVersion,
        contracts: '0.1.0',
      },
      futureModules: [
        'tests-management',
        'questions-management',
        'responses-dashboard',
        'permissions',
        'evaluator-agent-integration',
      ],
    });
    return;
  }

  if (requestUrl.pathname === '/tests') {
    const payload = await proxyAppsScriptRequest('tests');
    sendJson(res, payload.status === 'ok' ? 200 : 502, payload);
    return;
  }

  if (requestUrl.pathname === '/questions') {
    const testId = requestUrl.searchParams.get('testId') || 'critical-thinking-rubric';
    const payload = await proxyAppsScriptRequest('questions', { testId });
    sendJson(res, payload.status === 'ok' ? 200 : 502, payload);
    return;
  }

  response(res, 404, {
    status: 'error',
    message: 'Route not found',
  });
});

server.listen(port, () => {
  console.log(`Skillnea admin API listening on ${port}`);
});

function loadEnvFile() {
  const envPath = path.join(__dirname, '.env');
  if (!fs.existsSync(envPath)) {
    return;
  }

  const content = fs.readFileSync(envPath, 'utf8');
  content.split(/\r?\n/).forEach((line) => {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) {
      return;
    }

    const separatorIndex = trimmed.indexOf('=');
    if (separatorIndex === -1) {
      return;
    }

    const key = trimmed.slice(0, separatorIndex).trim();
    const value = trimmed.slice(separatorIndex + 1).trim();
    if (key && !process.env[key]) {
      process.env[key] = value;
    }
  });
}

async function proxyAppsScriptRequest(action, queryParams = {}) {
  if (!appsScriptBaseUrl) {
    return {
      status: 'error',
      message: 'APPS_SCRIPT_BASE_URL is not configured in admin-api.',
    };
  }

  try {
    const url = new URL(appsScriptBaseUrl);
    url.searchParams.set('action', action);
    Object.entries(queryParams).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.set(key, value);
      }
    });

    const remoteResponse = await fetch(url);
    const rawText = await remoteResponse.text();
    const normalizedText = rawText.trim();

    if (!normalizedText.startsWith('{') && !normalizedText.startsWith('[')) {
      return {
        status: 'error',
        message: 'Apps Script is returning HTML instead of JSON. Re-deploy the Web App with public access.',
        debug: {
          action,
          requestUrl: url.toString(),
          httpStatus: remoteResponse.status,
          responseSnippet: normalizedText.slice(0, 180),
        },
      };
    }

    const parsed = JSON.parse(normalizedText);
    return parsed;
  } catch (error) {
    return {
      status: 'error',
      message: error instanceof Error ? error.message : 'Unknown proxy error',
      debug: {
        action,
      },
    };
  }
}
