package dev.simonverhoeven.restdocsdemo.member;

import java.math.BigDecimal;

public record Member(String memberId, String fullName, Contact contact, BigDecimal dueBalance) {
}
