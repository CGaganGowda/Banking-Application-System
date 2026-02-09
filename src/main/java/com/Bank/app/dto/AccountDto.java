package com.Bank.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class AccountDto {
//    private Long id;
//    private String name;
//    private double balance;
//}

// Once it is defined as a record, the attributes will be final - immutable
public record AccountDto(Long id,
                         String name,
                         double balance){

}
