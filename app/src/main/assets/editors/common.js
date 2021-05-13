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
	console.log(`Canvas set to ${width}, ${height}`);
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

/**
 * 
 * @param {HTMLCanvasElement} [canvasElem] 
 * @returns {Promise<Blob>}
 */
function getImgBlobFromCanvas(canvasElem = canvas, mimeType = 'image/png') {
	return new Promise((res) => {
		canvasElem.toBlob((blob) => {
			res(blob);
		}, mimeType, 1);
	});
}

async function getImgSerializedFromCanvas(canvasElem = canvas, mimeType = 'image/png', callback) {
	const blob = await getImgBlobFromCanvas(canvasElem, mimeType);
	/** @type {string} */
	const base64 = await new Promise((res) => {
		const reader = new FileReader();
		reader.onloadend = () => {
			const base64WithMime = reader.result;
			const base64 = base64WithMime.split(',')[1];
			if (callback) {
				callback(base64);
			}
			res(base64);
		}
		reader.readAsDataURL(blob);
	});
	return base64;
}