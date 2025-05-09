document.addEventListener('DOMContentLoaded', function() {
    const confettiContainer = document.getElementById('confetti-container');
    const colors = ['red', 'blue', 'green', 'yellow', 'purple'];
    
    for (let i = 0; i < 100; i++) {
        createPaperConfetti();
    }
    
    function createPaperConfetti() {
        const paper = document.createElement('div');
        paper.classList.add('paper');
        
        const colorClass = colors[Math.floor(Math.random() * colors.length)];
        paper.classList.add(colorClass);
        
        const posX = Math.random() * window.innerWidth;
        const posY = -100 - (Math.random() * 500); 
        paper.style.left = posX + 'px';
        paper.style.top = posY + 'px';
        
        const rotation = Math.random() * 360;
        paper.style.transform = `rotate(${rotation}deg)`;
        
        const size = 8 + Math.random() * 7;
        paper.style.width = size + 'px';
        paper.style.height = size + 'px';
        
        // Animation
        const duration = 3 + Math.random() * 3; 
        const delay = Math.random() * 2; 
        
        paper.style.animation = `confetti-fall ${duration}s ease-out ${delay}s forwards`;
        
        confettiContainer.appendChild(paper);
        
        setTimeout(() => {
            paper.remove();
        }, (duration + delay) * 1000);
    }
    
    let confettiInterval = setInterval(() => {
        for (let i = 0; i < 20; i++) {
            createPaperConfetti();
        }
    }, 1000);
    
    setTimeout(() => {
        clearInterval(confettiInterval);
    }, 5000);
});