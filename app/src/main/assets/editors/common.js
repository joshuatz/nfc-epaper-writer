const canvas = document.querySelector('canvas');
const ctx = canvas.getContext('2d');

/**
 * Set the e-ink display size
 * @param {number} width
 * @param {number} height
 */
function setDisplaySize(width, height) {
	canvas.width = width;
	canvas.height = height;
	scaleCanvasToScreen();
	alert(`Canvas set to ${width}, ${height}`);
}


function scaleCanvasToScreen(canvasElem = canvas, margin = 20) {
	const {height: canvasHeight, width: canvasWidth} = canvasElem;
	let scale = 1;
	if (canvasHeight >= canvasWidth) {
		scale = (innerHeight - (margin * 2)) / canvasHeight;
	} else {
		scale = (innerWidth - (margin * 2)) / canvasWidth;
	}
	canvasElem.style.transform = `scale(${scale})`;
}

function clearCanvas() {
	ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function getBitmapFromCanvas(canvasElem = canvas) {
	//
}