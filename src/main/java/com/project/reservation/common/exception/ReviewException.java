package com.project.reservation.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ReviewException extends RuntimeException {

    private HttpStatus status;  //HTTP 상태 코드를 저장
    private String message;     //예외 메시지를 저장

    public ReviewException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
