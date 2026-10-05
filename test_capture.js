const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const chromePath = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const outDir = path.join(__dirname, 'screenshots');
if (!fs.existsSync(outDir)) {
    fs.mkdirSync(outDir, { recursive: true });
}

// Test capturing slide 1, 2, 4, 8, 17 of session 1
const slidesToTest = [
    { name: 's1_slide1', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s1/spring-security-s1.html#/0' },
    { name: 's1_slide2', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s1/spring-security-s1.html#/1' },
    { name: 's1_slide4', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s1/spring-security-s1.html#/3' },
    { name: 's1_slide8', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s1/spring-security-s1.html#/7' },
    { name: 's1_slide10', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s1/spring-security-s1.html#/9' },
    { name: 's1_slide17', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s1/spring-security-s1.html#/16' },
    { name: 's2_slide4', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s2/spring-security-s2.html#/3' },
    { name: 's2_slide10', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s2/spring-security-s2.html#/9' },
    { name: 's2_slide11', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s2/spring-security-s2.html#/10' },
    { name: 's2_slide13', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s2/spring-security-s2.html#/12' },
    { name: 's2_slide16', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s2/spring-security-s2.html#/15' },
    { name: 's2_slide17', url: 'http://localhost:8088/output-skills/slides-generator/10-03-2026_1/slides/spring-security-s2/spring-security-s2.html#/16' }
];

for (const s of slidesToTest) {
    const outFile = path.join(outDir, `${s.name}.png`);
    const cmd = `"${chromePath}" --headless --disable-gpu --screenshot="${outFile}" --window-size=1280,720 "${s.url}"`;
    try {
        execSync(cmd, { stdio: 'ignore' });
        console.log(`Captured: ${s.name}`);
    } catch (e) {
        console.error(`Error capturing ${s.name}:`, e.message);
    }
}
console.log('Capture finished.');
