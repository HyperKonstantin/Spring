package sc.springProject.sheduler;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseEntitySheduler {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final MeterRegistry meterRegistry;

    private AtomicInteger usersCount;
    private AtomicInteger departmentsCount;

    @PostConstruct
    public void init(){
        usersCount = new AtomicInteger();
        departmentsCount = new AtomicInteger();
        meterRegistry.gauge("users_in_database", usersCount);
        meterRegistry.gauge("departments_in_database", departmentsCount);
    }

    @Scheduled(fixedDelay = 2_000)
    public void updateDatabaseEntitiesCount(){
        usersCount.set(userRepository.findAll().size());
        departmentsCount.set(departmentRepository.findAll().size());
    }
}
