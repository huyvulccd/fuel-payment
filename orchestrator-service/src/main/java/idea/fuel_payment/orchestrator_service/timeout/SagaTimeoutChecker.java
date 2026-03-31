package idea.fuel_payment.orchestrator_service.timeout;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaInstance;
import idea.fuel_payment.orchestrator_service.domain.repository.SagaInstanceRepository;
import idea.fuel_payment.orchestrator_service.lock.SagaLockManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SagaTimeoutChecker {

	private final SagaInstanceRepository sagaInstanceRepository;
	private final SagaLockManager lockManager;

	@Value("${saga.timeout.default-step-timeout-seconds:60}")
	private int defaultTimeoutSeconds;

	/**
	 * Each 30 seconds, check saga instances timeout
	 */
	@Scheduled(fixedRate = 30000)
	@Transactional
	public void checkTimeouts() {
		LocalDateTime threshold = LocalDateTime.now()
				.minusSeconds(defaultTimeoutSeconds * 5L);

		List<SagaInstance> timedOutSagas =
				sagaInstanceRepository.findTimedOutSagas(threshold);

		for (SagaInstance saga : timedOutSagas) {
			log.warn("SAGA {} timed out! Order: {}, " +
							"currentStep: {}, lastUpdate: {}",
					saga.getSagaId(), saga.getOrderCode(),
					saga.getCurrentStep(), saga.getUpdatedAt());

			saga.fail("Saga timed out at step "
					+ saga.getCurrentStep());
			sagaInstanceRepository.save(saga);

			lockManager.releaseLock(saga.getOrderCode());
		}

		if (!timedOutSagas.isEmpty()) {
			log.info("Processed {} timed out sagas",
					timedOutSagas.size());
		}
	}
}