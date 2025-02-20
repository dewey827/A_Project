package com.project.reservation.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    //직렬화 ID. 초기값으로 1L
    private static final long serialVersionUID = 1L;

    String resourceName;    //찾을 수 없는 자원명
    String fieldName;       //필드명
    String fieldValue;      //필드값

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {

        //부모 클래스인 RuntimeExceptiond 의 생성자 호출.  String.format() 메소드로 동적 예외 메시지 생성. %s : 문자열 값을 삽입
        super(String.format("%s is not found [%s : %s]", resourceName, fieldName, fieldValue));

        //전달받은 매개변수 값을 클래스 필드에 저장
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
