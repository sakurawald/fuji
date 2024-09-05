package io.github.sakurawald.core.command.argument.wrapper.abst;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SingularValue<T> {
    T value;
}
