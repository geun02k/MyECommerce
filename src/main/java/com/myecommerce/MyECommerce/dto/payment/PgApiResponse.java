package com.myecommerce.MyECommerce.dto.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PgApiResponse<T> {

    private boolean success;
    private T data;
    private PgError error;

    /** PG 성공 응답 */
    public static <T> PgApiResponse<T> success(T data) {
        return PgApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /** PG 실패 응답 */
    public static <T> PgApiResponse<T> fail(String code, String message) {
        return PgApiResponse.<T>builder()
                .success(false)
                .error(new PgError(code, message))
                .build();
    }
}
