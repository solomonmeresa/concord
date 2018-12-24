package com.walmartlabs.concord.server.process.queue;

/*-
 * *****
 * Concord
 * -----
 * Copyright (C) 2017 - 2018 Walmart Inc.
 * -----
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =====
 */

import com.walmartlabs.concord.server.process.ProcessStatus;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Value.Immutable
public interface ProcessFilter {

    @Nullable
    Timestamp afterCreatedAt();

    @Nullable
    Timestamp beforeCreatedAt();

    @Value.Default
    default boolean includeWithoutProjects() {
        return false;
    }

    @Nullable
    String initiator();

    @Nullable
    Set<UUID> orgIds();

    @Nullable
    UUID parentId();

    @Nullable
    UUID projectId();

    @Nullable
    ProcessStatus status();

    @Nullable
    Set<String> tags();

    @Nullable
    Map<String, String> metaFilters();

    static ImmutableProcessFilter.Builder builder() {
        return ImmutableProcessFilter.builder();
    }
}
