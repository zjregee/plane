#include "config/config.h"

int main(int argc, char *argv[]) {
    Config config;
    config.parse_arg(argc, argv);

    WebServer server;
    server.init(config.PORT, config.LOGWrite, config.OPT_LINGER, config.TRIGMode, config.thread_num, config.close_log, config.actor_model);
    server.thread_pool();
    server.trig_mode();
    server.eventListen();
    server.eventLoop();
    
    return 0;
}