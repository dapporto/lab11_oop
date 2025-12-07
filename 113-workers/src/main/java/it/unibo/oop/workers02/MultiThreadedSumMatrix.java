package it.unibo.oop.workers02;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** 
 * An implementation of a class that sum double in a matrix.
 */
public class MultiThreadedSumMatrix implements SumMatrix {
    private static final int START_INDEX = 0;
    private final int nthread;

    /**
     * Create a new istance of this class.
     * 
     * @param nthread the number of threads to use
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    /**
     * {@InheritDoc}.
     */
    @Override
    public double sum(final double[][] matrix) {
        /* 
         * Creating a single list from the matrix
         */
        final List<Double> list = Arrays.stream(matrix)
            .flatMapToDouble(Arrays::stream)
            .boxed()
            .collect(Collectors.toList());

        final int size = list.size() % this.nthread + list.size() / this.nthread;

        return IntStream.iterate(START_INDEX, start -> start + size)
            .limit(nthread)
            .mapToObj(start -> new Worker(list, start, size))
            .peek(Thread::start)
            .peek(MultiThreadedSumMatrix::joinUninterruptibly)
            .mapToLong(Worker::getResult)
            .mapToDouble(i -> i)
            .sum();
    }

    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private static void joinUninterruptibly(final Thread target) {
        boolean pass = false;
        while (!pass) {
            try {
                target.join();
                pass = true;
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Worker extends Thread {
        private final List<Double> list;
        private final int startpos;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         *
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public synchronized void run() {
            // Println used to show the working ranges for debugging purposes
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1)); // NOPMD
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         *
         * @return the sum of every element in the array
         */
        public synchronized long getResult() {
            return this.res;
        }

    }
}
