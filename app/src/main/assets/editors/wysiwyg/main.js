// @ts-check

function main() {
	/** @type {HTMLCanvasElement} */
	const canvas = document.querySelector('canvas.main-canvas');
	const commonInstance = new NfcEIWCommon(canvas);
}

(() => {
	if (document.readyState === 'complete') {
		main();
	} else {
		window.addEventListener('load', main);
	}
})();