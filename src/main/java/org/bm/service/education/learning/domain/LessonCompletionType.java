package org.bm.service.education.learning.domain;

/**
 * Способ закрытия урока (подтверждение прохождения)
 */
public enum LessonCompletionType {
    /** Прохождение теста */
    TEST,
    /** Загрузка скриншота (подтверждение прохождения на внешнем ресурсе) */
    SCREENSHOT,
    /** Отметка о прочтении (без проверки) */
    READ
}
