package com.rubbertranslator.modules.textprocessor.post;

import com.google.gson.annotations.SerializedName;
import com.rubbertranslator.entity.Pair;

import java.util.Objects;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/13 8:54
 */
public class WordsPair extends Pair<String> {
    public WordsPair(String first, String second) {
        super(first, second);
    }
}
