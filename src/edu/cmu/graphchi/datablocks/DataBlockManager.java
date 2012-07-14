package edu.cmu.graphchi.datablocks;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright [2012] [Aapo Kyrola, Guy Blelloch, Carlos Guestrin / Carnegie Mellon University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *  Manages large chunks of data which are accessed using ChiPointers.
 * @author akyrola
 *         Date: 7/10/12
 */
public class DataBlockManager {

    private ArrayList<byte[]> blocks = new ArrayList<byte[]>(32678);



    public DataBlockManager() {

    }

    public int allocateBlock(int numBytes) {
        byte[] dataBlock = new byte[numBytes];

        synchronized(blocks) {
            int blockId = blocks.size();
            blocks.add(blockId, dataBlock);
            return blockId;
        }
    }

    public byte[] getRawBlock(int blockId) {
        return blocks.get(blockId);    /* Note, not synchronized! */
    }


    public void release(int blockId) {
        blocks.set(blockId, null);
    }

    public <T> T dereference(ChiPointer ptr, BytesToValueConverter<T> conv) {
        byte[] arr = new byte[conv.sizeOf()];
        System.arraycopy(getRawBlock(ptr.blockId), ptr.offset, arr, 0, arr.length);
        return conv.getValue(arr);
    }

    public <T> void writeValue(ChiPointer ptr, BytesToValueConverter<T> conv, T value) {
        byte[] arr = new byte[conv.sizeOf()];
        conv.setValue(arr, value);
        System.arraycopy(arr, 0, getRawBlock(ptr.blockId), ptr.offset, arr.length);
    }

}