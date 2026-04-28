const http = require('http');

const port = process.env.PORT || 8080;
const appsScriptBaseUrl = process.env.APPS_SCRIPT_BASE_URL || '';
const serviceVersion = process.env.SERVICE_VERSION || '0.1.0';

const response = (res, statusCode, payload) => {
  res.writeHead(statusCode, {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Content-Type': 'application/json',
  });
  res.end(JSON.stringify(payload));
};

const server = http.createServer((req, res) => {
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET,OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    });
    res.end();
    return;
  }

  if (req.url === '/health') {
    response(res, 200, {
      status: 'ok',
      service: 'skillnea-admin-api',
      version: serviceVersion,
    });
    return;
  }

  if (req.url === '/config') {
    response(res, 200, {
      status: 'ok',
      apiMode: appsScriptBaseUrl ? 'apps-script' : 'demo',
      appsScriptConfigured: Boolean(appsScriptBaseUrl),
      appsScriptBaseUrl,
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

  response(res, 404, {
    status: 'error',
    message: 'Route not found',
  });
});

server.listen(port, () => {
  console.log(`Skillnea admin API listening on ${port}`);
});
