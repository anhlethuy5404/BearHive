document.addEventListener('DOMContentLoaded', function () {
    const recordingTabs = document.querySelectorAll('.recording-tab');
    const audioPlayer = document.getElementById('audio-player');
    const playBtn = document.querySelector('.play-btn');
    const progressBar = document.querySelector('.progress-bar');
    const timeDisplay = document.querySelector('.time-display');
    const volumeControl = document.querySelector('.volume-control');
    const volumeLevel = document.querySelector('.volume-level');
    const nextBtn = document.querySelector('.next-btn');
    const submitBtn = document.querySelector('.submit-btn');
    const modalMessage = document.getElementById('modalMessage');
    const confirmSubmit = document.getElementById('confirmSubmit');
    const cancelSubmit = document.getElementById('cancelSubmit');
    const testId = document.getElementById('testId').value;
    let activePartId = recordingTabs[0]?.getAttribute('data-part-id');
    let progressWidth = 0;
    let progressInterval;
    let isPlaying = false;
    let timeTaken = 0;

    const timerInterval = setInterval(() => {
        timeTaken++;
    }, 1000);

    if (activePartId) {
        loadAudioForPart(activePartId);
        loadQuestionsForPart(activePartId);
    } else {
        console.error('No recording tabs found!');
    }

    //Modal
    function showModal(message, showCancelButton = true) {
        modalMessage.textContent = message;
        submitModal.style.display = 'flex';
        cancelSubmit.style.display = showCancelButton ? 'block' : 'none';
    }

    function hideModal() {
        submitModal.style.display = 'none';
    }

    // Tab switching 
    recordingTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            recordingTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            activePartId = tab.getAttribute('data-part-id');

            // Load audio
            audioPlayer.pause();
            playBtn.textContent = '▶';
            isPlaying = false;
            progressBar.style.width = '0%';
            timeDisplay.textContent = '00:00';
            loadAudioForPart(activePartId);
            loadQuestionsForPart(activePartId);
        });
    });
    function loadAudioForPart(partId) {
        const activeTab = document.querySelector(`.recording-tab[data-part-id="${partId}"]`);
        const audioUrl = activeTab.getAttribute('data-audio-url');
        if (audioUrl) {
            audioPlayer.src = audioUrl;
            audioPlayer.load();
        } else {
            console.error('Audio URL not found for part:', partId);
        }
    }

    function loadQuestionsForPart(partId) {
        document.querySelectorAll('.test-content').forEach(el => {
            el.style.display = 'none';
        });

        // Hiển thị nội dung của part hiện tại
        const testContent = document.querySelector(`#test-content-${partId}`);

        if (testContent) {
            testContent.style.display = 'block';
        } else {
            console.error(`Test content not found for partId: ${partId}`);
        }

        const activeTab = document.querySelector('.recording-tab.active');
        const nextTab = activeTab?.nextElementSibling;
        const nextBtn = document.querySelector('.next-btn');

        if (nextTab) {
            nextBtn.classList.remove('hidden');
        } else {
            nextBtn.classList.add('hidden');
        }
        updateQuestionButtons();
    }

    //tạo nút cuộn trang tới câu  hỏi t/u
    function updateQuestionButtons() {
        const questionBtns = document.querySelectorAll('.question-btn');
        const answerInputs = document.querySelectorAll('.answer-input');
        const radioInputs = document.querySelectorAll('.radio-input');
        const recordingTabs = document.querySelectorAll('.recording-tab');

        //FILL_IN_BLANK
        answerInputs.forEach((input, index) => {
            input.addEventListener('input', () => {
                const questionId = input.getAttribute('data-question-id');
                const btn = document.querySelector(`.question-btn[data-question-number="${questionId}"]`);
                if (input.value.trim() !== '') {
                    btn?.classList.add('answered');
                } else {
                    btn?.classList.remove('answered');
                }
            });
        });
        //MULTIPLE_CHOICE
        radioInputs.forEach(radio => {
            radio.addEventListener('change', () => {
                const questionId = radio.getAttribute('name').replace('question_', '');
                const btn = document.querySelector(`.question-btn[data-question-number="${questionId}"]`);
                if (radio.checked) {
                    btn?.classList.add('answered');
                }
            });
        });

        questionBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                questionBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');

                const questionNumber = btn.getAttribute('data-question-number');
                const btnPartId = btn.getAttribute('data-part-id-2');
                let activePartId = document.querySelector('.recording-tab.active')?.getAttribute('data-part-id');

                if (btnPartId !== activePartId) {
                    const targetTab = document.querySelector(`.recording-tab[data-part-id="${btnPartId}"]`);
                    if (targetTab) {
                        recordingTabs.forEach(t => t.classList.remove('active'));
                        targetTab.classList.add('active');
                        activePartId = btnPartId;
                        audioPlayer.pause();
                        playBtn.textContent = '▶';
                        isPlaying = false;
                        progressBar.style.width = '0%';
                        timeDisplay.textContent = '00:00';
                        loadAudioForPart(activePartId);
                        loadQuestionsForPart(activePartId);
                    }
                }
                const testContent = document.querySelector(`#test-content-${activePartId}`);
                if (testContent) {
                    const input = testContent.querySelector(`[data-question-id="${questionNumber}"]`);
                    if (input) {
                        input.scrollIntoView({ behavior: 'smooth' });
                        input.focus();
                    }
                }
            });
        });
    }
    // Play button toggle
    playBtn.addEventListener('click', function () {
        if (isPlaying) {
            audioPlayer.pause();
            this.textContent = '▶';
            clearInterval(progressInterval);
        } else {
            audioPlayer.play().catch(error => console.error('Error playing audio:', error));
            this.textContent = '⏸';
            simulateProgress();
        }
        isPlaying = !isPlaying;
    });

    // Simulate progress bar
    function simulateProgress() {
        clearInterval(progressInterval);
        progressInterval = setInterval(() => {
            if (!isNaN(audioPlayer.duration)) {
                const progress = (audioPlayer.currentTime / audioPlayer.duration) * 100;
                progressBar.style.width = `${progress}%`;

                const currentMinutes = Math.floor(audioPlayer.currentTime / 60);
                const currentSeconds = Math.floor(audioPlayer.currentTime % 60);
                timeDisplay.textContent = `${currentMinutes.toString().padStart(2, '0')}:${currentSeconds.toString().padStart(2, '0')}`;
            }
        }, 1000);
    }

    audioPlayer.addEventListener('ended', () => {
        isPlaying = false;
        playBtn.textContent = '▶';
        progressBar.style.width = '0%';
        timeDisplay.textContent = '00:00';
        clearInterval(progressInterval);
        audioPlayer.currentTime = 0;
    });

    // Volume control
    volumeControl.addEventListener('input', (e) => {
        const volume = e.target.value;
        audioPlayer.volume = volume;
        volumeLevel.style.width = `${volume * 100}%`;
    });
    // Next button
    nextBtn.addEventListener('click', () => {
        const currentTab = document.querySelector('.recording-tab.active');
        const nextTab = currentTab.nextElementSibling;
        if (nextTab) {
            recordingTabs.forEach(t => t.classList.remove('active'));
            nextTab.classList.add('active');
            activePartId = nextTab.getAttribute('data-part-id');

            audioPlayer.pause();
            playBtn.textContent = '▶';
            isPlaying = false;
            clearInterval(progressInterval);
            progressBar.style.width = '0%';
            timeDisplay.textContent = '00:00';
            loadAudioForPart(activePartId);
            loadQuestionsForPart(activePartId);
        }
    });
    // Submit button 
    submitBtn.addEventListener('click', () => {
        showModal('Bạn có chắc muốn nộp bài không?', true);
    });
    confirmSubmit.addEventListener('click', () => {
        hideModal();
        clearInterval(timerInterval);

        const answers = {}

        document.querySelectorAll('.option-item input[type="radio"]:checked').forEach(input => {
            const questionId = input.getAttribute('name').replace('question_', '');
            const optionText = input.value;
            const options = Array.from(input.closest('.option-item').parentElement.querySelectorAll('.option-item label'))
                .map(label => label.textContent.trim());
            const optionIndex = options.indexOf(optionText);
            answers[questionId] = String.fromCharCode(65 + optionIndex);
        });
        document.querySelectorAll('.answer-input').forEach(input => {
            const questionId = input.getAttribute('data-question-id');
            const answerText = input.value.trim();
            if (answerText) {
                answers[questionId] = answerText;
            }
        });

        fetch(`/ielts/test/submit/${testId}?timeTaken=${timeTaken}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(answers),
        })
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                } else if (!response.ok) {
                    throw new Error('Gửi bài thất bại');
                }
            })
            .catch(error => {
                console.error('Lỗi khi gửi bài:', error);
                showModal('Gửi bài thất bại, vui lòng thử lại.', false);
            });
    });
    cancelSubmit.addEventListener('click', () => {
        hideModal();
    });

    // Question navigation
    const questionBtns = document.querySelectorAll('.question-btn');
    questionBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            questionBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
        });
    });

    // Timer countdown
    let timeLeft = parseInt(document.getElementById('test-time-limit').value) * 60;
    const timerDisplay = document.querySelector('.timer-display');

    function updateTimer() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        timerDisplay.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

        if (timeLeft > 0) {
            timeLeft--;
        }
        else {
            showModal('Đã hết giờ làm bài, bài sẽ tự động nộp.', false);
            clearInterval(timerInterval);

            const answers = {}

            document.querySelectorAll('.option-item input[type="radio"]:checked').forEach(input => {
                const questionId = input.getAttribute('name').replace('question_', '');
                const optionText = input.value;
                const options = Array.from(input.closest('.option-item').parentElement.querySelectorAll('.option-item label'))
                    .map(label => label.textContent.trim());
                const optionIndex = options.indexOf(optionText);
                answers[questionId] = String.fromCharCode(65 + optionIndex);
            });
            document.querySelectorAll('.answer-input').forEach(input => {
                const questionId = input.getAttribute('data-question-id');
                const answerText = input.value.trim();
                if (answerText) {
                    answers[questionId] = answerText;
                }
            });

            fetch(`/ielts/test/submit/${testId}?timeTaken=${timeTaken}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(answers),
            })
                .then(response => {
                    if (response.redirected) {
                        window.location.href = response.url;
                    } else if (!response.ok) {
                        throw new Error('Gửi bài thất bại');
                    }
                })
                .catch(error => {
                    console.error('Lỗi khi gửi bài:', error);
                    showModal('Gửi bài thất bại, vui lòng thử lại.', false);
                });
            clearInterval(timerInterval);
        }
    }

    // Update timer mỗi giây
    setInterval(updateTimer, 1000);
});