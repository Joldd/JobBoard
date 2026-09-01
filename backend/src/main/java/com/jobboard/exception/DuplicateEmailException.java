package com.jobboard.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Un compte existe déjà pour l'email : " + email);
    }
}
