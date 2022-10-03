package com.edu.ulab.app.util;

import org.springframework.jdbc.support.KeyHolder;

public interface KeyHolderFactory {
    KeyHolder newKeyHolder();
}
