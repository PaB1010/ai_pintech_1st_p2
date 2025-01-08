package org.koreait.admin.board.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.board.controllers.RequestBoard;
import org.koreait.board.repositories.BoardRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Lazy
@Component("adminBoardValidator")
@RequiredArgsConstructor
public class BoardValidator implements Validator {

    private final BoardRepository boardRepository;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.isAssignableFrom(RequestBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        // 이미 커맨드 객체에서 Err 있을경우
        if (errors.hasErrors()) return;

        RequestBoard form = (RequestBoard) target;
        String bid = form.getBid();

        // 게시판 등록 모드 && 게시판 아이디 중복
        // 게시판 수정 모드일 경우에는 항상 중복이므로 등록 모드일 경우만
        if (form.getMode().equals("add") && boardRepository.existsById(bid)) {

            errors.rejectValue("bid", "Duplicated");
        }
    }
}