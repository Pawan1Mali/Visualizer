/* =============================================
   SORTING VISUALIZER — APP LOGIC
   ============================================= */

// ---- State ----
let currentArray = [];
let sortSteps = [];
let animationTimer = null;
let currentStepIndex = 0;
let isSorting = false;
let startTime = null;
let timerInterval = null;

// ---- Algorithm Info Cache ----
let algorithmsInfo = {};

// ---- DOM Elements ----
const algorithmSelect = document.getElementById('algorithmSelect');
const sizeSlider = document.getElementById('sizeSlider');
const sizeValue = document.getElementById('sizeValue');
const speedSlider = document.getElementById('speedSlider');
const speedValue = document.getElementById('speedValue');
const generateBtn = document.getElementById('generateBtn');
const sortBtn = document.getElementById('sortBtn');
const stopBtn = document.getElementById('stopBtn');
const visualizerContainer = document.getElementById('visualizerContainer');
const comparisonsCount = document.getElementById('comparisonsCount');
const swapsCount = document.getElementById('swapsCount');
const statusText = document.getElementById('statusText');
const timeElapsed = document.getElementById('timeElapsed');
const algoName = document.getElementById('algoName');
const algoDescription = document.getElementById('algoDescription');
const timeBest = document.getElementById('timeBest');
const timeAvg = document.getElementById('timeAvg');
const timeWorst = document.getElementById('timeWorst');
const spaceComplexity = document.getElementById('spaceComplexity');
const isStable = document.getElementById('isStable');

// ---- Speed Mapping ----
function getDelay() {
    const val = parseInt(speedSlider.value);
    // Maps 1-100 to 500ms-1ms (logarithmic feel)
    return Math.max(1, Math.round(500 * Math.pow(0.95, val)));
}

function getSpeedLabel(val) {
    if (val <= 20) return 'Slow';
    if (val <= 40) return 'Medium';
    if (val <= 60) return 'Fast';
    if (val <= 80) return 'Very Fast';
    return 'Ultra';
}

// ---- Array Generation ----
function generateArray() {
    if (isSorting) return;
    const size = parseInt(sizeSlider.value);
    currentArray = [];
    for (let i = 0; i < size; i++) {
        currentArray.push(Math.floor(Math.random() * 95) + 5); // 5 to 99
    }
    renderBars(currentArray);
    resetStats();
}

// ---- Bar Rendering ----
function renderBars(array, comparing = [], swapping = [], sorted = [], overwriting = []) {
    const container = visualizerContainer;
    const maxVal = 100;
    const containerHeight = container.clientHeight - 8;

    // If bar count changed, rebuild DOM
    if (container.children.length !== array.length) {
        container.innerHTML = '';
        const fragment = document.createDocumentFragment();
        for (let i = 0; i < array.length; i++) {
            const bar = document.createElement('div');
            bar.className = 'sort-bar';
            bar.style.height = `${(array[i] / maxVal) * containerHeight}px`;
            fragment.appendChild(bar);
        }
        container.appendChild(fragment);
    } else {
        // Update existing bars
        const bars = container.children;
        for (let i = 0; i < array.length; i++) {
            const bar = bars[i];
            bar.style.height = `${(array[i] / maxVal) * containerHeight}px`;

            // Reset classes
            bar.className = 'sort-bar';

            if (swapping.includes(i)) {
                bar.classList.add('swapping');
            } else if (overwriting.includes(i)) {
                bar.classList.add('overwriting');
            } else if (comparing.includes(i)) {
                bar.classList.add('comparing');
            } else if (sorted.includes(i)) {
                bar.classList.add('sorted');
            }
        }
    }
}

// ---- Stats ----
function resetStats() {
    comparisonsCount.textContent = '0';
    swapsCount.textContent = '0';
    statusText.textContent = 'Ready';
    statusText.className = 'stat-value status-text';
    timeElapsed.textContent = '0.0s';
    if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
    }
}

function updateStats(step) {
    comparisonsCount.textContent = step.comparisons.toLocaleString();
    swapsCount.textContent = step.swaps.toLocaleString();
}

function startTimer() {
    startTime = performance.now();
    timerInterval = setInterval(() => {
        const elapsed = ((performance.now() - startTime) / 1000).toFixed(1);
        timeElapsed.textContent = `${elapsed}s`;
    }, 100);
}

function stopTimer() {
    if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
    }
    if (startTime) {
        const elapsed = ((performance.now() - startTime) / 1000).toFixed(1);
        timeElapsed.textContent = `${elapsed}s`;
    }
}

// ---- Algorithm Info ----
async function loadAlgorithmsInfo() {
    try {
        const response = await fetch('/api/algorithms');
        const data = await response.json();
        data.forEach(algo => {
            algorithmsInfo[algo.id] = algo;
        });
        updateAlgoInfo();
    } catch (err) {
        console.error('Failed to load algorithms info:', err);
    }
}

function updateAlgoInfo() {
    const algoId = algorithmSelect.value;
    const info = algorithmsInfo[algoId];
    if (info) {
        algoName.textContent = info.name;
        algoDescription.textContent = info.description;
        timeBest.textContent = info.timeBest;
        timeAvg.textContent = info.timeAvg;
        timeWorst.textContent = info.timeWorst;
        spaceComplexity.textContent = info.space;
        isStable.textContent = info.stable;
    }
}

// ---- Sorting ----
async function startSort() {
    if (isSorting) return;
    if (currentArray.length === 0) {
        generateArray();
    }

    isSorting = true;
    sortBtn.disabled = true;
    generateBtn.disabled = true;
    sizeSlider.disabled = true;
    algorithmSelect.disabled = true;
    stopBtn.disabled = false;

    statusText.textContent = 'Sorting...';
    statusText.className = 'stat-value status-text sorting';
    resetStats();
    startTimer();

    try {
        const response = await fetch('/api/sort', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                algorithm: algorithmSelect.value,
                array: currentArray
            })
        });

        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }

        sortSteps = await response.json();
        currentStepIndex = 0;

        // Animate through steps
        animateSteps();

    } catch (err) {
        console.error('Sort request failed:', err);
        statusText.textContent = 'Error!';
        stopSort();
    }
}

function animateSteps() {
    if (!isSorting || currentStepIndex >= sortSteps.length) {
        finishSort();
        return;
    }

    const step = sortSteps[currentStepIndex];

    // Determine highlighted indices
    let comparing = [];
    let swapping = [];
    let overwriting = [];
    let sorted = step.sortedIndices || [];

    if (step.type === 'COMPARE') {
        comparing = [step.idx1, step.idx2].filter(i => i >= 0);
    } else if (step.type === 'SWAP') {
        swapping = [step.idx1, step.idx2].filter(i => i >= 0);
    } else if (step.type === 'OVERWRITE') {
        overwriting = [step.idx1].filter(i => i >= 0);
    } else if (step.type === 'DONE') {
        sorted = step.sortedIndices || [];
    }

    renderBars(step.array, comparing, swapping, sorted, overwriting);
    updateStats(step);

    currentStepIndex++;

    if (step.type === 'DONE') {
        finishSort();
        return;
    }

    animationTimer = setTimeout(() => animateSteps(), getDelay());
}

function finishSort() {
    isSorting = false;
    stopTimer();

    // Final state: all bars sorted
    if (sortSteps.length > 0) {
        const lastStep = sortSteps[sortSteps.length - 1];
        currentArray = Array.from(lastStep.array);

        // Show all bars as sorted with celebration
        const allSorted = [];
        for (let i = 0; i < currentArray.length; i++) {
            allSorted.push(i);
        }
        renderBars(currentArray, [], [], allSorted, []);

        // Celebration cascade
        const bars = visualizerContainer.children;
        for (let i = 0; i < bars.length; i++) {
            setTimeout(() => {
                bars[i].classList.add('celebrate');
            }, i * 15);
        }
    }

    statusText.textContent = 'Complete!';
    statusText.className = 'stat-value status-text done';

    sortBtn.disabled = false;
    generateBtn.disabled = false;
    sizeSlider.disabled = false;
    algorithmSelect.disabled = false;
    stopBtn.disabled = true;
}

function stopSort() {
    isSorting = false;
    if (animationTimer) {
        clearTimeout(animationTimer);
        animationTimer = null;
    }
    stopTimer();

    statusText.textContent = 'Stopped';
    statusText.className = 'stat-value status-text';

    sortBtn.disabled = false;
    generateBtn.disabled = false;
    sizeSlider.disabled = false;
    algorithmSelect.disabled = false;
    stopBtn.disabled = true;
}

// ---- Event Listeners ----
generateBtn.addEventListener('click', generateArray);
sortBtn.addEventListener('click', startSort);
stopBtn.addEventListener('click', stopSort);

sizeSlider.addEventListener('input', () => {
    sizeValue.textContent = sizeSlider.value;
    if (!isSorting) generateArray();
});

speedSlider.addEventListener('input', () => {
    speedValue.textContent = getSpeedLabel(parseInt(speedSlider.value));
});

algorithmSelect.addEventListener('change', updateAlgoInfo);

// ---- Handle window resize ----
let resizeTimeout;
window.addEventListener('resize', () => {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        if (!isSorting && currentArray.length > 0) {
            renderBars(currentArray);
        }
    }, 150);
});

// ---- Initialize ----
document.addEventListener('DOMContentLoaded', () => {
    loadAlgorithmsInfo();
    generateArray();
    speedValue.textContent = getSpeedLabel(parseInt(speedSlider.value));
});
