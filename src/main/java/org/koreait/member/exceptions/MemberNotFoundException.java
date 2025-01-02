package org.koreait.member.exceptions;

import org.koreait.global.exceptions.scripts.AlertBackException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends AlertBackException {

    public MemberNotFoundException() {

        super("NofFound.member", HttpStatus.NOT_FOUND);

        setErrorCode(true);
    }
}