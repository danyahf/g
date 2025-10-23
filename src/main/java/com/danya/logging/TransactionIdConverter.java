package com.danya.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

public class TransactionIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String txId = MDC.get("transactionId");
        return (txId != null && !txId.isBlank()) ? " | txId=" + txId : "";
    }
}
