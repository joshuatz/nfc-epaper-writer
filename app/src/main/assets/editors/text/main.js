// @ts-check
const textInput = document.querySelector('textarea');
const defaultText = textInput.value;
let inverted = false;
let bgColor = 'white';
let textColor = 'black';

/**
 * Adapted from https://stackoverflow.com/a/65433398/11447682
 * @param {string} text
 * @param {string} fontFace
 */
function fitAndFillTextCenter(text, fontFace = 'Arial') {
	const { height: canvasH, width: canvasW } = canvas;
	let fontSize = getFontSizeToFit(text, fontFace, canvasW, canvasH);
	ctx.fillStyle = textColor;
	ctx.font = fontSize + `px ${fontFace}`;

	ctx.textBaseline = 'middle';
	ctx.textAlign = 'center';

	const x = 0;
	const y = 0;
	const lines = text.match(/[^\r\n]+/g);
	for (let i = 0; i < lines.length; i++) {
		let xL = (canvasW - x) / 2;
		let yL = y + (canvasH / (lines.length + 1)) * (i + 1);

		ctx.fillText(lines[i], xL, yL);
	}
}

/**
 * Adapted from https://stackoverflow.com/a/65433398/11447682
 * @param {string} text
 * @param {string} fontFace
 * @param {number} width
 * @param {number} height
 */
function getFontSizeToFit(text, fontFace, width, height) {
	ctx.font = `1px ${fontFace}`;

	let fitFontWidth = Number.MAX_VALUE;
	const lines = text.match(/[^\r\n]+/g);
	lines.forEach((line) => {
		fitFontWidth = Math.min(fitFontWidth, width / ctx.measureText(line).width);
	});
	let fitFontHeight = height / (lines.length * 1.2); // if you want more spacing between line, you can increase this value
	return Math.min(fitFontHeight, fitFontWidth);
}

/**
 * Normal operation is black text on white, but you can set inverted
 * @param {boolean} [updatedInverted]
 */
function setInverted(updatedInverted) {
	inverted = typeof updatedInverted === 'boolean' ? updatedInverted : !inverted;
	if (inverted) {
		bgColor = 'black';
		textColor = 'white';
	} else {
		bgColor = 'white';
		textColor = 'black';
	}
	renderToCanvas();
}

function drawBg() {
	ctx.fillStyle = bgColor;
	ctx.fillRect(0, 0, canvas.width, canvas.height);
}

function renderToCanvas() {
	clearCanvas();
	drawBg();
	fitAndFillTextCenter(textInput.value);
}

renderToCanvas();
setTimeout(renderToCanvas, 200);

// Attach listeners
textInput.addEventListener('keyup', renderToCanvas);
document.querySelector('button#addLineBreak').addEventListener('click', () => {
	textInput.value += '\n';
});
document.querySelector('button#reset').addEventListener('click', () => {
	textInput.value = defaultText;
	setInverted(false);
	renderToCanvas();
});
document.querySelector('button#setInverted').addEventListener('click', () => {
	setInverted();
});
