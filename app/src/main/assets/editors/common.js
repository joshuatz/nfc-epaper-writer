// @ts-check

// Prefix class name to make this less likely to throw on 3rd party sites
class NfcEIWCommon {
	/** @type {HTMLCanvasElement} */
	canvas;
	/** @type {CanvasRenderingContext2D} */
	ctx;

	/** @param {HTMLCanvasElement} canvasElem */
	constructor(canvasElem) {
		this.canvas = canvasElem;
		this.ctx = this.canvas.getContext('2d');
		window['setDisplaySize'] = this.setDisplaySize.bind(this);
		window['getImgSerializedFromCanvas'] = this.getImgSerializedFromCanvas.bind(this);
	}

	/**
	 * Set the e-ink display size
	 * @param {number} width
	 * @param {number} height
	 */
	setDisplaySize(width, height) {
		// @ts-ignore
		if (typeof window.resize_canvas_and_save_dimensions === 'function') {
			// jspaint
			// @ts-ignore
			resize_canvas_and_save_dimensions(width, height);
		} else {
			this.canvas.width = width;
			this.canvas.height = height;
		}
		this.scaleCanvasToScreen();
		console.log(`Canvas set to ${width}, ${height}`);
	}

	scaleCanvasToScreen(canvasElem = this.canvas, margin = 20) {
		const {height: canvasHeight, width: canvasWidth} = canvasElem;
		let scale = 1;
		if (canvasHeight >= canvasWidth) {
			scale = (innerHeight - (margin * 2)) / canvasHeight;
		} else {
			scale = (innerWidth - (margin * 2)) / canvasWidth;
		}
		canvasElem.style.transform = `scale(${scale})`;
	}

	clearCanvas() {
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
	}

	/**
	 * 
	 * @param {HTMLCanvasElement} [canvasElem] 
	 * @returns {Promise<Blob>}
	 */
	getImgBlobFromCanvas(canvasElem = this.canvas, mimeType = 'image/png') {
		return new Promise((res) => {
			canvasElem.toBlob((blob) => {
				res(blob);
			}, mimeType, 1);
		});
	}

	async getImgSerializedFromCanvas(canvasElem = this.canvas, mimeType = 'image/png', callback) {
		const blob = await this.getImgBlobFromCanvas(canvasElem, mimeType);
		/** @type {string} */
		const base64 = await new Promise((res) => {
			const reader = new FileReader();
			reader.onloadend = () => {
				/** @type {string} */
				const base64WithMime = (reader.result);
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
}


/**
 * Inject Script
 */

// (() => {
// 	const scriptSrc = 'https://appassets.androidplatform.net/assets/editors/common.js';
// 	let injected = !!document.querySelector(`script[src="${scriptSrc}"]`);

// 	const runInjection = () => {
// 		const domTarget = document.head || document.body;
// 		if (!injected && domTarget) {
// 			const scriptElem = document.createElement('script');
// 			scriptElem.src = scriptSrc;
// 			domTarget.appendChild(scriptElem);
// 			injected = true;
// 			console.log('Editor Common JS injected!');
// 		}
// 	}
// 	runInjection();

// 	if (!injected) {
// 		window.addEventListener('DOMContentLoaded', runInjection);
// 		setTimeout(runInjection, 200);
// 	} else {
// 		console.log('Skipping Common JS injection - already injected');
// 	}
// })();