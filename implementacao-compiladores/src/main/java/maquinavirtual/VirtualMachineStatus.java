package maquinavirtual;

public enum VirtualMachineStatus {
    NOT_STARTED,
    RUNNING,
    SYSCALL_IO_WRITE,
    SYSCALL_IO_READ,
    HALTED
}