package dev.simonverhoeven.restdocsdemo.member;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String memberId) {
        super("The member with ID '" + memberId + "' wasn't found");
    }
}
