package io.github.sakurawald.core.command.argument.wrapper.abst;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingularValue<T> {
    T value;
}
