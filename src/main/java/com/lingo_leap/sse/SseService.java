package com.lingo_leap.sse;

import com.lingo_leap.rabbit.consumer.ReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);

    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();
    private List<ReportDto> latestReport;

    public synchronized SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> {
            removeEmitter(emitter);
            emitter.complete();
        });
        emitter.onError(error -> removeEmitter(emitter));

        log.info("SSE client connected; active clients: {}", emitters.size());
        if (latestReport != null) {
            sendReport(emitter, latestReport);
        }
        return emitter;
    }

    public synchronized void sendReport(List<ReportDto> report) {
        latestReport = List.copyOf(report);
        log.info("Broadcasting SSE report to {} client(s)", emitters.size());
        for (SseEmitter emitter : emitters) {
            sendReport(emitter, latestReport);
        }
    }

    private void sendReport(SseEmitter emitter, List<ReportDto> report) {
        try {
            emitter.send(SseEmitter.event()
                    .name("quiz-report")
                    .data(report, MediaType.APPLICATION_JSON));
        } catch (IOException | IllegalStateException e) {
            log.warn("Removing disconnected SSE client", e);
            removeEmitter(emitter);
            emitter.complete();
        }
    }

    private void removeEmitter(SseEmitter emitter) {
        if (emitters.remove(emitter)) {
            log.info("SSE client disconnected; active clients: {}", emitters.size());
        }
    }
}
