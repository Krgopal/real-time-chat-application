package com.demo.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionInfo {
    private Integer httpStatusCode;
    private String code;
    private String userMessage;
    private String description;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ExceptionInfo)) {
            return false;
        } else {
            ExceptionInfo other = (ExceptionInfo)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$httpStatusCode = this.getHttpStatusCode();
                    Object other$httpStatusCode = other.getHttpStatusCode();
                    if (this$httpStatusCode == null) {
                        if (other$httpStatusCode == null) {
                            break label59;
                        }
                    } else if (this$httpStatusCode.equals(other$httpStatusCode)) {
                        break label59;
                    }

                    return false;
                }

                Object this$code = this.getCode();
                Object other$code = other.getCode();
                if (this$code == null) {
                    if (other$code != null) {
                        return false;
                    }
                } else if (!this$code.equals(other$code)) {
                    return false;
                }

                Object this$userMessage = this.getUserMessage();
                Object other$userMessage = other.getUserMessage();
                if (this$userMessage == null) {
                    if (other$userMessage != null) {
                        return false;
                    }
                } else if (!this$userMessage.equals(other$userMessage)) {
                    return false;
                }

                Object this$description = this.getDescription();
                Object other$description = other.getDescription();
                if (this$description == null) {
                    if (other$description != null) {
                        return false;
                    }
                } else if (!this$description.equals(other$description)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExceptionInfo;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $httpStatusCode = this.getHttpStatusCode();
        result = result * 59 + ($httpStatusCode == null ? 43 : $httpStatusCode.hashCode());
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $userMessage = this.getUserMessage();
        result = result * 59 + ($userMessage == null ? 43 : $userMessage.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "CustomExceptionInfo(httpStatusCode=" + this.getHttpStatusCode() + ", code=" + this.getCode() + ", userMessage=" + this.getUserMessage() + ", description=" + this.getDescription() + ")";
    }
}

