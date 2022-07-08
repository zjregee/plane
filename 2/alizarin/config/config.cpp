#include "config.h"

Config::Config() {
    PORT = 8080;
    TRIGMode = 0;
    OPT_LINGER = 0;
    thread_num = 8;
    test = 0;
}

void Config::parse_arg(int argc, char*argv[]) {
    int opt;
    const char *str = "p:m:o:n:t:";
    while ((opt = getopt(argc, argv, str)) != -1) {
        switch (opt) {
        case 'p': {
            PORT = atoi(optarg);
            break;
        }
        case 'm': {
            TRIGMode = atoi(optarg);
            break;
        }
        case 'o': {
            OPT_LINGER = atoi(optarg);
            break;
        }
        case 'n': {
            thread_num = atoi(optarg);
            break;
        }
        case 't': {
            test = atoi(optarg);
            break;
        }
        default:
            break;
        }
    }
}