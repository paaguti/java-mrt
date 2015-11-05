Testing
=======

Is you want to make sure your changes did not break anything especially for the support of
draft-petrie-grow-mrt-add-path please execute the following:

* For disabled add path support:
```bash
java -cp mrt.jar org.javamrt.progs.MRT_ByteToAscii test/dump_as51861_addpath_disabled_bird_1.5.0_patched.mrt > test/dump_as51861_addpath_disabled_bird_1.5.0_patched.txt_new
```

```bash
diff test/dump_as51861_addpath_disabled_bird_1.5.0_patched.txt_new test/dump_as51861_addpath_disabled_bird_1.5.0_patched.txt
```
The diff command should not finish without showing any differences.

* For enabled add path support:
```bash
java -cp mrt.jar org.javamrt.progs.MRT_ByteToAscii test/dump_as51861_addpath_enabled_bird_1.5.0_patched.mrt > test/dump_as51861_addpath_enabled_bird_1.5.0_patched.txt_new
```

```bash
diff test/dump_as51861_addpath_enabled_bird_1.5.0_patched.txt_new test/dump_as51861_addpath_enabled_bird_1.5.0_patched.txt
```
The diff command should not finish without showing any differences.