// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
(async () => {
const redmineUrl = 'http://127.0.0.1:3020';
const redmineIssueRegex = new RegExp(`^${redmineUrl}/issues/([0-9]+)`);
function transfer(script) {
    return new Promise((resolve, reject) => {
        const ws = new WebSocket('ws://127.0.0.1:48113');
        ws.addEventListener('open', () => {
            ws.send(script);
        });
        const timeout = setTimeout(() => {
            ws.close();
            reject(new Error('WebSocket response timeout'));
        }, 10000);
        ws.addEventListener('message', (event) => {
            clearTimeout(timeout);
            console.log('Response:', event.data);
            ws.close();
            resolve(event.data);
        });
        ws.addEventListener('error', (event) => {
            clearTimeout(timeout);
            console.error('WebSocket error:', event);
            reject(new Error('WebSocket error'));
        });
        ws.addEventListener('close', (event) => {
            if (!event.wasClean) {
                clearTimeout(timeout);
                reject(new Error(`WebSocket closed unexpectedly: code=${event.code}`));
            }
        });
    });
}
function autoAlert(message, durationMs = 5000) {
    const overlay = document.createElement('div');
    overlay.style.cssText = `
font-family: Lato, Roboto, Arial, sans-serif;
position: fixed; top: 20px; left: 50%; transform: translateX(-50%);
background: #333; color: #fff; padding: 16px 32px;
border: 4px solid #ee5500;
border-radius: 8px; font-size: 16px; z-index: 9999;
box-shadow: 0 4px 12px rgba(0,0,0,0.3);
transition: opacity 0.3s ease;
`;
    overlay.textContent = message;
    document.body.appendChild(overlay);
    setTimeout(() => {
        overlay.style.opacity = '0';
        setTimeout(() => overlay.remove(), 300);
    }, durationMs);
}
const match = location.href.match(redmineIssueRegex);
if (!match) {
    autoAlert('Run me while on an issue page in Redmine');
    return;
}
const issueId = match[1];
const apiKeyResp = await fetch(`${redmineUrl}/my/api_key`);
    if (!apiKeyResp.ok) throw new Error(`Failed to fetch API key: ${apiKeyResp.status}`);
    const apiKeyHtml = await apiKeyResp.text();
    const preEl = new DOMParser()
        .parseFromString(apiKeyHtml, 'text/html')
        .querySelector('#content pre');
if (!preEl) {
    const msg = 'Could not retrieve API key. Are you signed in to Redmine?';
    autoAlert(msg);
    throw new Error(msg);
}
const apiKey = preEl.textContent.trim();
const issueResp = await fetch(`${redmineUrl}/issues/${issueId}.json`, {
    headers: { 'X-Redmine-API-Key': apiKey }
});
if (!issueResp.ok) {
    const msg = `Failed to fetch issue: ${issueResp.status}`;
    autoAlert(msg);
    throw new Error(msg);
}
const response = await issueResp.json();
const issue = response.issue;
const fp = {
    [`ID_${issueId}`]: {
        '@core': `${issueId}  [${issue.assigned_to?.name ?? issue.author.name}]`,
        '@link': location.href,
        '@details': issue.subject,
        '@attributes': {[`proj.${(new Date()).toISOString().split('T')[0]}`]: issue.project.name},
    }
};
const fpJson = JSON.stringify(fp);
const toBase64 = str => btoa(unescape(encodeURIComponent(str)));
const jsonBase64 = toBase64(fpJson);
const script = `c.select(io.github.macmarrum.freeplane.Import.fromJsonStringBase64('${jsonBase64}', node))`;
autoAlert(await transfer(script));
})().catch(console.error);
