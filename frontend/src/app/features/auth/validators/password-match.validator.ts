import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Validateur de groupe : compare deux champs du même FormGroup (password / confirmPassword). */
export function passwordMatchValidator(passwordKey: string, confirmKey: string): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get(passwordKey)?.value;
    const confirmPassword = group.get(confirmKey)?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  };
}
