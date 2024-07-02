package com.toomeet.user.auth.dto;

import com.toomeet.user.user.Gender;
import com.toomeet.user.validator.AgeValidator;
import com.toomeet.user.validator.EnumValidator;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
@Builder
public class AccountRegisterDto {


    @NotNull
    @Email(message = "Email không hợp lệ!")
    private String email;

    @NotEmpty(message = "Thiếu mật khẩu")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Mật khẩu phải có ít nhất 8 ký tự, 1 ký tự số, 1 ký tự đặc biệt, bao gồm ký tự in thường và ký tự in hoa, không có khoảng trắng")
    private String password;

    @NotNull
    @EnumValidator(message = "Giới tính không hợp lệ", enumClass = Gender.class)
    private Gender gender;

    @Past()
    @AgeValidator(value = 16, message = "Người dùng chưa đủ 16 tuổi")
    @NotNull
    private Date dateOfBirth;

    @NotEmpty(message = "Thiếu tên người dùng")
    @Size(min = 5, message = "Tên người dùng phải có ít nhất 5 kí tự! ")
    @Size(max = 50, message = "Tên người dùng quá dài!")
    private String name;


}
