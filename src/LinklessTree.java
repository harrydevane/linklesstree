public class LinklessTree<E extends Comparable<? super E>> {

    private static final int INITIAL_SIZE = 15;

    private Object[] elements;
    private int[] sizes;

    public LinklessTree() {
        elements = new Object[INITIAL_SIZE];
        sizes = new int[INITIAL_SIZE];
        sizes[0] = 0;
    }

    public int size() {
        return getSize(0);
    }

    public E getValue(int index) {
        return (E) elements[index];
    }

    public int getSize(int subtree) {
        if (subtree >= elements.length) {
            return 0;
        }
        return sizes[subtree];
    }

    public int findIndex(E element) {
        int currentIndex = 0;
        E currentElement;
        while (currentIndex < elements.length && (currentElement = (E) elements[currentIndex]) != null) {
            int value = element.compareTo(currentElement);
            if (value == 0) {
                return currentIndex;
            }
            currentIndex = (currentIndex * 2) + (value < 0 ? 1 : 2);
        }
        return currentIndex;
    }

    public boolean contains(E element) {
        int currentIndex = 0;
        E currentElement;
        while (currentIndex < elements.length && (currentElement = (E) elements[currentIndex]) != null) {
            int value = element.compareTo(currentElement);
            if (value == 0) {
                return true;
            }
            currentIndex = (currentIndex * 2) + (value < 0 ? 1 : 2);
        }
        return false;
    }

    private void grow() {
        Object[] newElements = new Object[elements.length * 2];
        int[] newSizes = new int[sizes.length * 2];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        System.arraycopy(sizes, 0, newSizes, 0, sizes.length);
        elements = newElements;
        sizes = newSizes;
    }

    public E get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        int currentIndex = 0;
        while (currentIndex < elements.length) {
            int leftSize = getSize((currentIndex * 2) + 1);
            if (index == leftSize) {
                return (E) elements[currentIndex];
            } else if (index > leftSize) {
                index -= (leftSize + 1);
                currentIndex = (currentIndex * 2) + 2;
            } else {
                currentIndex = (currentIndex * 2) + 1;
            }
        }
        return null;
    }

    public boolean insert(E element) {
        int currentIndex = 0;
        E currentElement;
        while (currentIndex < elements.length && (currentElement = (E) elements[currentIndex]) != null) {
            int value = element.compareTo(currentElement);
            if (value == 0) {
                return false; // duplicate
            }
            currentIndex = (currentIndex * 2) + (value < 0 ? 1 : 2);
        }
        while (currentIndex >= elements.length) {
            grow();
        }
        elements[currentIndex] = element;
        modifySizes(0, currentIndex, element, 1);
        return true;
    }

    public boolean delete(E element) {
        int currentIndex = 0;
        E currentElement;
        while (currentIndex < elements.length && (currentElement = (E) elements[currentIndex]) != null) {
            int value = element.compareTo(currentElement);
            if (value == 0) {
                int leftSize = sizes[(currentIndex * 2) + 1];
                int rightSize = sizes[(currentIndex * 2) + 2];
                modifySizes(0, currentIndex, element, -1);
                if (leftSize > 0 && leftSize >= rightSize) {
                    elements[currentIndex] = deleteLargest((currentIndex * 2) + 1);
                } else if (rightSize > leftSize) {
                    elements[currentIndex] = deleteSmallest((currentIndex * 2) + 2);
                } else {
                    elements[currentIndex] = null;
                }
                return true;
            }
            currentIndex = (currentIndex * 2) + (value < 0 ? 1 : 2);
        }
        return false;
    }

    private E deleteLargest(int subtree) {
        E element = (E) elements[subtree];
        sizes[subtree] = sizes[subtree] - 1;
        if (elements.length > (subtree * 2) + 2) {
            if (elements[(subtree * 2) + 2] != null) {
                return deleteLargest((subtree * 2) + 2);
            }
        }
        elements[subtree] = null;
        return element;
    }

    private E deleteSmallest(int subtree) {
        E element = (E) elements[subtree];
        sizes[subtree] = sizes[subtree] - 1;
        if (elements.length > (subtree * 2) + 1) {
            if (elements[(subtree * 2) + 1] != null) {
                return deleteSmallest((subtree * 2) + 1);
            }
        }
        elements[subtree] = null;
        return element;
    }

    private void modifySizes(int startIndex, int endIndex, E element, int diff) {
        int currentIndex = startIndex;
        E currentElement;
        while (currentIndex <= endIndex && (currentElement = (E) elements[currentIndex]) != null) {
            sizes[currentIndex] = sizes[currentIndex] + diff;
            int value = element.compareTo(currentElement);
            if (value == 0) {
                break;
            }
            currentIndex = (currentIndex * 2) + (value < 0 ? 1 : 2);
        }
    }
}
