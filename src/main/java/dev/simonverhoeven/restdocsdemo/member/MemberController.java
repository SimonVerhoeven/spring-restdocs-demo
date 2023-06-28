package dev.simonverhoeven.restdocsdemo.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final List<Member> members;

    public MemberController() {
        members = Stream.of(
                new Member("0819B178E55090F09730BDAA2B9AC1F329CD1244", "Simon Verhoeven", new Contact("some@email.com", "+32999999999"), BigDecimal.valueOf(1_000_000L))
        ).collect(Collectors.toList());
    }

    @GetMapping("/{memberId}")
    public Member getBook(@PathVariable String memberId) {
        return members.stream().filter(member -> memberId.equals(member.memberId())).findFirst().orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleBookNotFound(MemberNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Member not found");
        problemDetail.setType(URI.create("https://somelibrary.com/member/not-found"));
        return problemDetail;
    }
}
