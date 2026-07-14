package ai.chat2db.community.start.test;

import ai.chat2db.community.start.Application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Indexed;


@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Indexed
public class TestApplication {
}
