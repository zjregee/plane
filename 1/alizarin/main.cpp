#include "config/config.h"

int main(int argc, char *argv[]) {
    Config config;
    config.parse_arg(argc, argv);

    WebServer server;
    server.init(config.PORT, config.OPT_LINGER, config.TRIGMode, config.thread_num, config.test);
    server.thread_pool();
    server.trig_mode();
    server.eventListen();
    server.eventLoop();
    
    return 0;
}