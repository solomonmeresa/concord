package com.walmartlabs.concord.runtime.v2.runner.vm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmartlabs.concord.runtime.v2.runner.PersistenceService;
import com.walmartlabs.concord.sdk.Constants;
import com.walmartlabs.concord.svm.Runtime;
import com.walmartlabs.concord.svm.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SaveLastErrorCommandTest {

    @Test
    public void test() throws Exception {
        ObjectMapper om = new ObjectMapper();

        PersistenceService persistenceService = mock(PersistenceService.class);

        Runtime runtime = mock(Runtime.class);
        when(runtime.getService(eq(ObjectMapper.class))).thenReturn(om);
        when(runtime.getService(eq(PersistenceService.class))).thenReturn(persistenceService);

        MyException error = new MyException("BOOM1");
        error.setSomeCyclicField(error);

        Frame rootFrame = Frame.builder()
                .root()
                .locals(Collections.singletonMap(Frame.LAST_EXCEPTION_KEY, error))
                .build();
        State state = new InMemoryState(rootFrame);
        ThreadId threadId = state.getRootThreadId();

        // ---
        SaveLastErrorCommand cmd = new SaveLastErrorCommand();
        rootFrame.push(new SaveLastErrorCommand());

        try {
            cmd.eval(runtime, state, threadId);
        } catch (MyException e) {
            assertEquals(error, e);
        }

        ArgumentCaptor<PersistenceService.Writer> writerCaptor = ArgumentCaptor.forClass(PersistenceService.Writer.class);
        verify(persistenceService, times(1))
                .persistFile(eq(Constants.Files.OUT_VALUES_FILE_NAME), writerCaptor.capture());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writerCaptor.getValue().write(bos);

        assertThat(bos.toString(), containsString("\"message\":\"BOOM1\""));
        assertThat(bos.toString(), containsString("\"someCyclicField\":1"));
        assertThat(bos.toString(), containsString("\"@id\":1"));
    }

    @SuppressWarnings("unused")
    static class MyException extends Exception {

        private Exception someCyclicField;

        public MyException(String message) {
            super(message);
        }

        public Exception getSomeCyclicField() {
            return someCyclicField;
        }

        public void setSomeCyclicField(Exception someCyclicField) {
            this.someCyclicField = someCyclicField;
        }
    }
}