// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
(async () => {
const stripHist = 1;
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
overflow: hidden;`;
    message.split('\n').forEach((line, i) => {
        if (i > 0) overlay.appendChild(document.createElement('br'));
        overlay.appendChild(document.createTextNode(line));
    });
    const bar = document.createElement('div');
    bar.style.cssText = `
position: absolute; bottom: 0; left: 0;
height: 2px; width: 100%; background: #ee5500;
animation: _shrink ${durationMs}ms linear forwards;`;
    if (!document.getElementById('_autoAlertStyle')) {
        const style = document.createElement('style');
        style.id = '_autoAlertStyle';
        style.textContent = `@keyframes _shrink { from { width: 100% } to { width: 0% } }`;
        document.head.appendChild(style);
    }
    overlay.appendChild(bar);
    document.body.appendChild(overlay);
    setTimeout(() => {
        overlay.style.opacity = '0';
        setTimeout(() => overlay.remove(), 300);
    }, durationMs);
}

function extractGmailMessages(messageSelector) {
    const rxThreadId = new RegExp('https://mail.google.com/mail/u/\\d/#(?:inbox|all|sent)/([A-Za-z]+)');
    const m = location.href.match(rxThreadId);
    if (!m) {
        const msg = 'Could not find thread ID in URL.';
        autoAlert(msg);
        throw new Error(msg);
    }
    const threadId = m[1];
    let threadInner = {'@link': location.href.replace(new RegExp('/#(all|sent)/'), '/#inbox/')};
    const messages = document.querySelectorAll(messageSelector);
    messages.forEach(message => {
        const messageData = extractGmailMessageData(message);
        if (messageData) {
            threadInner = {...threadInner, ...messageData};
        }
    });
    return {[threadId]: threadInner};
 }

const removeAfterWrote = text => stripHist === 0 ? text : text.split(/(?<=<[a-z0-9.-]+@[a-z0-9]+\.[a-z]+> (?:napisał\(a\)|wrote):)\r?\n/m, 1)[0];
const removeAfterFromLine = text => stripHist === 0 ? text : text.split(/\r?\n(\*From:\*|From:) +[^<]+ +<[a-z0-9.-]+@[a-z0-9]+\.[a-z]+>/, 1)[0];
const removeAfterSigHead = text => text.split(/^-- $/m, 1)[0];
const removeBlockquotes = text => text.replace(/^>.*(\r?\n|\r)?/gm, '');
const removeExtraLines = text => text.replace(/\r?\n\r?\n[\u00A0 \t]*(\r?\n)/g, '$1');

function extractGmailMessageData(messageEl) {
    if (!messageEl) {
        const msg = 'Gmail message element not found.';
        autoAlert(msg);
        throw new Error(msg);
    }
    const msgId = messageEl.getAttribute('data-message-id') || `${Date.now()}`;
    const fromEl = messageEl.querySelector('.gD[email]');
    const from_emailAddressOnly = fromEl ? fromEl.getAttribute('email').trim() : '';
    let to_emailAddressOnly = '';
    let cc_emailAddressOnly = '';
    const ajwContainer = messageEl.querySelector('.ajw');
    if (ajwContainer) {
        const toList = [];
        const ccList = [];
        const recipientSpans = ajwContainer.querySelectorAll('span[email]');
        recipientSpans.forEach(span => {
            const email = span.getAttribute('email');
            if (!email) return;
            let isCC = false;
            let sibling = span.previousSibling;
            while (sibling) {
                const text = (sibling.textContent || '').toLowerCase();
                if (text.includes('cc:') || text.includes('dw:') || text.includes('do wiadomości')) {
                    isCC = true;
                    break;
                }
                if (text.includes('do:') || text.includes('to:')) {
                    break;
                }
                sibling = sibling.previousSibling;
            }
            if (isCC) {
                ccList.push(email);
            } else {
                toList.push(email);
            }
        });
        to_emailAddressOnly = toList.join(', ');
        cc_emailAddressOnly = ccList.join(', ');
    }
    const subjectEl = document.querySelector('h2.hP') || document.querySelector('h1.ha') || messageEl.querySelector('.hP');
    const subject = subjectEl ? subjectEl.innerText.trim() : 'No Subject';
    const dateEl = messageEl.querySelector('.g3[title]') || messageEl.querySelector('.g3');
    const rawDate = dateEl ? (dateEl.getAttribute('title') || dateEl.innerText) : '';
    const formattedDate = parseGmailDate(rawDate);
    const bodyEl = messageEl.querySelector('.a3s');
//    const body = bodyEl ? bodyEl.innerHTML.trim() : '';
//    const body = bodyEl ? convertHtmlToMarkdown(bodyEl) : '';
    const body = bodyEl ? removeExtraLines(removeBlockquotes(removeAfterSigHead(removeAfterFromLine(removeAfterWrote(convertHtmlToPlainText(bodyEl)))))) : '';
    return { [msgId]: {
        '@core': '|from|' + from_emailAddressOnly + '|\n' +
                 '|-|-|\n' +
                 '|to|' + to_emailAddressOnly + '|\n' +
                 '|cc|' + cc_emailAddressOnly + '|\n' +
                 '|subject|' + subject + '|\n' +
                 '|date|' + formattedDate + '|\n',
        'body': {
            '@core': body,
            '@style': '+max20cm'
        },
        '@props': {
          'style.name': '=Markdown',
          'minimized': true,
          'folded': true
        }
    }};
}

function parseGmailDate(dateStr) {
    if (!dateStr) return '';
    const normalized = dateStr.toLowerCase().replace(/\s+/g, ' ').trim();
    const monthMap = {
        'sty': '01', 'stycznia': '01', 'styczeń': '01',
        'lut': '02', 'lutego': '02', 'luty': '02',
        'mar': '03', 'marca': '03', 'marzec': '03',
        'kwi': '04', 'kwietnia': '04', 'kwiecień': '04',
        'maj': '05', 'maja': '05',
        'cze': '06', 'czerwca': '06', 'czerwiec': '06',
        'lip': '07', 'lipca': '07', 'lipiec': '07',
        'sie': '08', 'sierpnia': '08', 'sierpień': '08',
        'wrz': '09', 'września': '09', 'wrzesień': '09',
        'paź': '10', 'października': '10', 'październik': '10',
        'lis': '11', 'listopada': '11', 'listopad': '11',
        'gru': '12', 'grudnia': '12', 'grudzień': '12',
        'jan': '01', 'feb': '02', 'apr': '04', 'may': '05',
        'jun': '06', 'jul': '07', 'aug': '08', 'sep': '09',
        'oct': '10', 'nov': '11', 'dec': '12'
    };
    const parsedPl = normalized.match(/^(\d{1,2})\s+([a-ząęółśżźćń]+)\s+(\d{4}),?\s+(\d{1,2}):(\d{2})/);
    if (parsedPl) {
        const day = parsedPl[1].padStart(2, '0');
        const monthWord = parsedPl[2];
        const year = parsedPl[3];
        const hour = parsedPl[4].padStart(2, '0');
        const min = parsedPl[5];
        const month = monthMap[monthWord.substring(0, 3)] || monthMap[monthWord] || '01';
        return year + '-' + month + '-' + day + ' ' + hour + ':' + min;
    }
    try {
        const d = new Date(dateStr);
        if (!isNaN(d.getTime())) {
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            const hour = String(d.getHours()).padStart(2, '0');
            const min = String(d.getMinutes()).padStart(2, '0');
            return year + '-' + month + '-' + day + ' ' + hour + ':' + min;
        }
    } catch (e) {
    }
    return dateStr;
}

function convertHtmlToMarkdown(element) {
    if (!element) return '';
    let markdown = '';
    function walk(node, listContext = null) {
        if (node.nodeType === Node.TEXT_NODE) {
            markdown += node.textContent.replace(/[ \t]+/g, ' ');
            return;
        }
        if (node.nodeType !== Node.ELEMENT_NODE) return;
        const tagName = node.tagName.toLowerCase();
        if (node.classList.contains('ajU') || node.style.display === 'none') {
            return;
        }
        let currentListContext = listContext;
        switch (tagName) {
            case 'h1': markdown += '\n\n# '; break;
            case 'h2': markdown += '\n\n## '; break;
            case 'h3': markdown += '\n\n### '; break;
            case 'h4': markdown += '\n\n#### '; break;
            case 'p': markdown += '\n\n'; break;
            case 'div': markdown += '\n'; break;
            case 'br': markdown += '\n'; break;
            case 'strong': case 'b': markdown += '**'; break;
            case 'em': case 'i': markdown += '*'; break;
            case 'a': markdown += '['; break;
            case 'ul':
                currentListContext = { type: 'ul' };
                markdown += '\n';
                break;
            case 'ol':
                currentListContext = { type: 'ol', index: 1 };
                markdown += '\n';
                break;
            case 'li':
                if (listContext && listContext.type === 'ol') {
                    markdown += '\n';
                    markdown += listContext.index++ + '. ';
                } else {
                    markdown += '\n* ';
                }
                break;
            case 'blockquote': markdown += '\n\n> '; break;
        }
        for (let child of node.childNodes) {
            walk(child, currentListContext);
        }
        switch (tagName) {
            case 'strong': case 'b': markdown += '**'; break;
            case 'em': case 'i': markdown += '*'; break;
            case 'a':
                const href = node.getAttribute('href') || '';
                markdown += '](' + href + ')';
                break;
            case 'p': markdown += '\n\n'; break;
            case 'blockquote': markdown += '\n\n'; break;
            case 'ul': case 'ol': markdown += '\n'; break;
        }
    }
    walk(element);
    return markdown
        .replace(/\r\n|\r/g, '\n')
        .replace(/[ \t]+/g, ' ')
        .replace(/\n{3,}/g, '\n\n')
        .replace(/^\s+|\s+$/g, '');
}

function convertHtmlToPlainText(element) {
    if (!element) return '';
    let text = '';
    function walk(node) {
        if (node.nodeType === Node.TEXT_NODE) {
            text += node.textContent.replace(/[ \t]+/g, ' ');
            return;
        }
        if (node.nodeType !== Node.ELEMENT_NODE) return;
        const tagName = node.tagName.toLowerCase();
        if (node.classList.contains('ajU') || node.style.display === 'none') {
            return;
        }
        switch (tagName) {
            case 'h1': case 'h2': case 'h3': case 'h4':
            case 'p': case 'blockquote':
                text += '\n\n';
                break;
            case 'div': case 'br': case 'li':
                text += '\n';
                break;
        }
        for (let child of node.childNodes) {
            walk(child);
        }
        switch (tagName) {
            case 'p': case 'blockquote':
                text += '\n\n';
                break;
        }
    }
    walk(element);
    return text
        .replace(/\r\n|\r/g, '\n')
        .replace(/[ \t]+/g, ' ')
        .replace(/\n{3,}/g, '\n\n')
        .replace(/^\s+|\s+$/g, '');
}

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

const messages = extractGmailMessages('div.adn.ads');
const fpJson = JSON.stringify(messages);
if (fpJson.length > 1_000_000) {
    autoAlert('Too much data!\nOver 1,000,000 characters.');
    return;
}
const toBase64 = str => btoa(unescape(encodeURIComponent(str)));
const jsonBase64 = toBase64(fpJson);
function splitIntoChunks(str, chunkSize = 65535) {
    const chunks = [];
    for (let i = 0; i < str.length; i += chunkSize) {
        chunks.push(str.slice(i, i + chunkSize));
    }
    return chunks;
}
let script = 'def sb = new StringBuilder()\n';
splitIntoChunks(jsonBase64, 65535).forEach(chunk => script += `sb.append('${chunk}')\n`);
script += 'c.select(io.github.macmarrum.freeplane.Import.fromJsonStringBase64(sb.toString(), node))';
autoAlert(await transfer(script), 2000);
})().catch(console.error);
