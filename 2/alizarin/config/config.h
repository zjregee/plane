#ifndef CONFIG_H
#define CONFIG_H

#include "../webserver.h"

using namespace std;

class Config {
public:
    Config();
    ~Config(){};
    void parse_arg(int argc, char*argv[]);
    int PORT;
    int TRIGMode;
    int OPT_LINGER;
    int thread_num;
    int test;
};

#endif