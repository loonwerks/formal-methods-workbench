cmake_minimum_required(VERSION 3.8.2)

project (PROC_SW C)



DeclareCAmkESComponent(RadioDriver_Impl
  SOURCES components/RadioDriver_Impl/src/tb_RadioDriver_Impl.c
  INCLUDES components/RadioDriver_Impl/includes/ includes
)

DeclareCAmkESComponent(FlightPlanner_Impl
  SOURCES components/FlightPlanner_Impl/src/tb_FlightPlanner_Impl.c
  INCLUDES components/FlightPlanner_Impl/includes/ includes
)

DeclareCAmkESComponent(WaypointManager_Impl
  SOURCES components/WaypointManager_Impl/src/tb_WaypointManager_Impl.c
  INCLUDES components/WaypointManager_Impl/includes/ includes
)

DeclareCAmkESComponent(UARTDriver_Impl
  SOURCES components/UARTDriver_Impl/src/tb_UARTDriver_Impl.c
  INCLUDES components/UARTDriver_Impl/includes/ includes
)

DeclareCAmkESComponent(tb_FPLN_recv_map_Monitor
  SOURCES components/tb_Monitors/tb_FPLN_recv_map_Monitor/src/tb_FPLN_recv_map_Monitor.c
  INCLUDES components/tb_Monitors/tb_FPLN_recv_map_Monitor/includes/ includes
)

DeclareCAmkESComponent(tb_WPM_flight_plan_Monitor
  SOURCES components/tb_Monitors/tb_WPM_flight_plan_Monitor/src/tb_WPM_flight_plan_Monitor.c
  INCLUDES components/tb_Monitors/tb_WPM_flight_plan_Monitor/includes/ includes
)

DeclareCAmkESComponent(tb_UART_waypoint_in_Monitor
  SOURCES components/tb_Monitors/tb_UART_waypoint_in_Monitor/src/tb_UART_waypoint_in_Monitor.c
  INCLUDES components/tb_Monitors/tb_UART_waypoint_in_Monitor/includes/ includes
)

DeclareCAmkESComponent(tb_FPLN_position_status_Monitor
  SOURCES components/tb_Monitors/tb_FPLN_position_status_Monitor/src/tb_FPLN_position_status_Monitor.c
  INCLUDES components/tb_Monitors/tb_FPLN_position_status_Monitor/includes/ includes
)

DeclareCAmkESComponent(tb_WPM_position_status_Monitor
  SOURCES components/tb_Monitors/tb_WPM_position_status_Monitor/src/tb_WPM_position_status_Monitor.c
  INCLUDES components/tb_Monitors/tb_WPM_position_status_Monitor/includes/ includes
)

DeclareCAmkESComponent(tb_RADIO_send_status_in_Monitor
  SOURCES components/tb_Monitors/tb_RADIO_send_status_in_Monitor/src/tb_RADIO_send_status_in_Monitor.c
  INCLUDES components/tb_Monitors/tb_RADIO_send_status_in_Monitor/includes/ includes
)

DeclareCAmkESRootserver(PROC_SW.camkes)
