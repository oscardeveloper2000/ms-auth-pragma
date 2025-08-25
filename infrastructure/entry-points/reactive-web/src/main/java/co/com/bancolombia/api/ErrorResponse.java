package co.com.bancolombia.api;

import lombok.Data;

// Modelo de respuesta de error (puedes moverlo a un archivo propio si lo prefieres)
@Data
public class ErrorResponse {
    private String correlationId;
    private ErrorHeader errorHeader;
    private ErrorDetail errorDetail;

    public static Builder builder() { return new Builder(); }

    // getters, setters, builder...

    public static class Builder {
        private final ErrorResponse instance = new ErrorResponse();
        public Builder correlationId(String id) { instance.correlationId = id; return this; }
        public Builder errorHeader(ErrorResponse.ErrorHeader h) { instance.errorHeader = h; return this; }
        public Builder errorDetail(ErrorResponse.ErrorDetail d) { instance.errorDetail = d; return this; }
        public ErrorResponse build() { return instance; }
    }
    @Data
    public static class ErrorHeader {
        private int returnCode;
        private String message;
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final ErrorHeader instance = new ErrorHeader();
            public Builder returnCode(int c) { instance.returnCode = c; return this; }
            public Builder message(String m) { instance.message = m; return this; }
            public ErrorHeader build() { return instance; }
        }
    }
    @Data
    public static class ErrorDetail {
        private String code;
        private String message;
        private String errorDate;
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final ErrorDetail instance = new ErrorDetail();
            public Builder code(String c) { instance.code = c; return this; }
            public Builder message(String m) { instance.message = m; return this; }
            public Builder errorDate(String d) { instance.errorDate = d; return this; }
            public ErrorDetail build() { return instance; }
        }
    }
}