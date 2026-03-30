export class SpinnerOnButton {
  constructor(buttonId) {
    this.button = document.getElementById(buttonId);
    if (!this.button) {
      throw new Error(`Button with id "${buttonId}" not found`);
    }

    this.spinnerEl = null;
    this.checkmarkEl = null;
    this.crossEl = null;
    this.originalDisabledState = this.button.disabled;
  }

  run() {
    // Désactive le bouton
    this.button.disabled = true;

    // Crée et ajoute le spinner s'il n'existe pas
    if (!this.spinnerEl) {
      this.spinnerEl = document.createElement('div');
      this.spinnerEl.className = 'btn-icon spinner';
      this.button.appendChild(this.spinnerEl);
    }

    // Affiche le spinner
    this.spinnerEl.classList.add('active');

    // Cache les autres icônes si elles existent
    if (this.checkmarkEl) this.checkmarkEl.classList.remove('active');
    if (this.crossEl) this.crossEl.classList.remove('active');
  }

  success(delay = 2000) {
    // Cache le spinner
    if (this.spinnerEl) {
      this.spinnerEl.classList.remove('active');
    }

    // Crée et ajoute le checkmark s'il n'existe pas
    if (!this.checkmarkEl) {
      this.checkmarkEl = this._createCheckmark();
      this.button.appendChild(this.checkmarkEl);
    }

    // Affiche le checkmark
    this.checkmarkEl.classList.add('active');

    // Réinitialise après le délai
    setTimeout(() => {
      this.reset();
    }, delay);
  }

  error(delay = 2000) {
    // Cache le spinner
    if (this.spinnerEl) {
      this.spinnerEl.classList.remove('active');
    }

    // Crée et ajoute la croix si elle n'existe pas
    if (!this.crossEl) {
      this.crossEl = this._createCross();
      this.button.appendChild(this.crossEl);
    }

    // Affiche la croix
    this.crossEl.classList.add('active');

    // Réinitialise après le délai
    setTimeout(() => {
      this.reset();
    }, delay);
  }

  reset() {
    // Cache toutes les icônes
    if (this.spinnerEl) this.spinnerEl.classList.remove('active');
    if (this.checkmarkEl) this.checkmarkEl.classList.remove('active');
    if (this.crossEl) this.crossEl.classList.remove('active');

    // Réactive le bouton
    this.button.disabled = this.originalDisabledState;
  }

  _createCheckmark() {
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('class', 'btn-icon checkmark');
    svg.setAttribute('viewBox', '0 0 52 52');

    const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    circle.setAttribute('class', 'checkmark-circle');
    circle.setAttribute('cx', '26');
    circle.setAttribute('cy', '26');
    circle.setAttribute('r', '25');

    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    path.setAttribute('class', 'checkmark-check');
    path.setAttribute('d', 'M14.1 27.2l7.1 7.2 16.7-16.8');

    svg.appendChild(circle);
    svg.appendChild(path);

    return svg;
  }

  _createCross() {
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('class', 'btn-icon cross');
    svg.setAttribute('viewBox', '0 0 52 52');

    const line1 = document.createElementNS('http://www.w3.org/2000/svg', 'line');
    line1.setAttribute('class', 'cross-line');
    line1.setAttribute('x1', '16');
    line1.setAttribute('y1', '16');
    line1.setAttribute('x2', '36');
    line1.setAttribute('y2', '36');

    const line2 = document.createElementNS('http://www.w3.org/2000/svg', 'line');
    line2.setAttribute('class', 'cross-line');
    line2.setAttribute('x1', '36');
    line2.setAttribute('y1', '16');
    line2.setAttribute('x2', '16');
    line2.setAttribute('y2', '36');

    svg.appendChild(line1);
    svg.appendChild(line2);

    return svg;
  }
}
