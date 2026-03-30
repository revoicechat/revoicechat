package fr.revoicechat.core.repository.page;

import java.util.List;

public record PageResult<T>(List<T> content, int size, long totalElements) {
}
