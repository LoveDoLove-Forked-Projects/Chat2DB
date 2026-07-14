package ai.chat2db.community.start.test.common;

import ai.chat2db.community.start.Application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public abstract class BaseTest {

}
