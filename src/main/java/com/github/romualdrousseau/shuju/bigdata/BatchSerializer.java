package com.github.romualdrousseau.shuju.bigdata;

import java.io.IOException;

public interface BatchSerializer {

    byte[] serialize(Row[] batch) throws IOException;

    Row[] deserialize(byte[] bytes) throws IOException;
}
