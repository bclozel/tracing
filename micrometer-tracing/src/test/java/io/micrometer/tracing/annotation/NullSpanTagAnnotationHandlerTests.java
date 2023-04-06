/*
 * Copyright 2023 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.annotation.TagValueExpressionResolver;
import io.micrometer.common.annotation.TagValueResolver;
import io.micrometer.tracing.SpanCustomizer;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class NullSpanTagAnnotationHandlerTests {

    TagValueResolver tagValueResolver = parameter -> null;

    TagValueExpressionResolver tagValueExpressionResolver = (expression, parameter) -> "";

    SpanCustomizer spanCustomizer = new SpanCustomizer() {
        @Override
        public SpanCustomizer name(String name) {
            return this;
        }

        @Override
        public SpanCustomizer tag(String key, String value) {
            return this;
        }

        @Override
        public SpanCustomizer event(String value) {
            return this;
        }
    };

    SpanTagAnnotationHandler handler = new SpanTagAnnotationHandler(aClass -> tagValueResolver,
            aClass -> tagValueExpressionResolver);

    @Test
    void shouldUseEmptyStringWheCustomTagValueResolverReturnsNull() throws NoSuchMethodException, SecurityException {
        Method method = AnnotationMockClass.class.getMethod("getAnnotationForTagValueResolver", String.class);
        Annotation annotation = method.getParameterAnnotations()[0][0];
        if (annotation instanceof SpanTag) {
            String resolvedValue = this.handler.resolveTagValue((SpanTag) annotation, "test",
                    aClass -> tagValueResolver, aClass -> tagValueExpressionResolver);
            assertThat(resolvedValue).isEqualTo("");
        }
        else {
            fail("Annotation was not SpanTag");
        }
    }

    @Test
    void shouldUseEmptyStringWhenTagValueExpressionReturnNull() throws NoSuchMethodException, SecurityException {
        Method method = AnnotationMockClass.class.getMethod("getAnnotationForTagValueExpression", String.class);
        Annotation annotation = method.getParameterAnnotations()[0][0];
        if (annotation instanceof SpanTag) {
            String resolvedValue = this.handler.resolveTagValue((SpanTag) annotation, "test",
                    aClass -> tagValueResolver, aClass -> tagValueExpressionResolver);

            assertThat(resolvedValue).isEqualTo("");
        }
        else {
            fail("Annotation was not SpanTag");
        }
    }

    @Test
    void shouldUseEmptyStringWhenArgumentIsNull() throws NoSuchMethodException, SecurityException {
        Method method = AnnotationMockClass.class.getMethod("getAnnotationForArgumentToString", Long.class);
        Annotation annotation = method.getParameterAnnotations()[0][0];
        if (annotation instanceof SpanTag) {
            String resolvedValue = this.handler.resolveTagValue((SpanTag) annotation, null, aClass -> tagValueResolver,
                    aClass -> tagValueExpressionResolver);
            assertThat(resolvedValue).isEqualTo("");
        }
        else {
            fail("Annotation was not SpanTag");
        }
    }

    protected class AnnotationMockClass {

        @NewSpan
        public void getAnnotationForTagValueResolver(
                @SpanTag(key = "test", resolver = TagValueResolver.class) String test) {
        }

        @NewSpan
        public void getAnnotationForTagValueExpression(@SpanTag(key = "test", expression = "null") String test) {
        }

        @NewSpan
        public void getAnnotationForArgumentToString(@SpanTag("test") Long param) {
        }

    }

}
