-- apply changes
create table rc_quests_players (
  id                            integer auto_increment not null,
  player                        varchar(255) not null,
  player_id                     varchar(40),
  constraint uq_rc_quests_players_player unique (player),
  constraint pk_rc_quests_players primary key (id)
);

create table rc_quests_player_objectives (
  id                            integer auto_increment not null,
  quest_id                      integer not null,
  objective_id                  integer not null,
  completion_time               datetime(6),
  abortion_time                 datetime(6),
  constraint pk_rc_quests_player_objectives primary key (id)
);

create table rc_quests_player_quests (
  id                            integer auto_increment not null,
  player_id                     integer not null,
  quest                         varchar(255),
  phase                         varchar(20),
  start_time                    datetime(6),
  completion_time               datetime(6),
  abortion_time                 datetime(6),
  quest_pool_id                 integer,
  constraint ck_rc_quests_player_quests_phase check ( phase in ('NOT_STARTED','IN_PROGRESS','OBJECTIVES_COMPLETED','COMPLETE','ABORTED')),
  constraint pk_rc_quests_player_quests primary key (id)
);

create table rc_quests_player_pools (
  id                            integer auto_increment not null,
  player_id                     integer not null,
  quest_pool                    varchar(255),
  last_start                    datetime(6),
  last_completion               datetime(6),
  last_reset                    datetime(6),
  successive_quest_counter      integer not null,
  constraint pk_rc_quests_player_pools primary key (id)
);

create table rc_quests_player_tasks (
  id                            integer auto_increment not null,
  objective_id                  integer not null,
  task_id                       integer not null,
  completion_time               datetime(6),
  abortion_time                 datetime(6),
  constraint pk_rc_quests_player_tasks primary key (id)
);

create table rc_quests_player_quest_items (
  id                            integer auto_increment not null,
  player                        varchar(40),
  slot                          integer not null,
  inventory_id                  integer not null,
  object_storage_id             integer not null,
  constraint pk_rc_quests_player_quest_items primary key (id)
);

create index ix_rc_quests_player_objectives_quest_id on rc_quests_player_objectives (quest_id);
alter table rc_quests_player_objectives add constraint fk_rc_quests_player_objectives_quest_id foreign key (quest_id) references rc_quests_player_quests (id) on delete restrict on update restrict;

create index ix_rc_quests_player_quests_player_id on rc_quests_player_quests (player_id);
alter table rc_quests_player_quests add constraint fk_rc_quests_player_quests_player_id foreign key (player_id) references rc_quests_players (id) on delete restrict on update restrict;

create index ix_rc_quests_player_quests_quest_pool_id on rc_quests_player_quests (quest_pool_id);
alter table rc_quests_player_quests add constraint fk_rc_quests_player_quests_quest_pool_id foreign key (quest_pool_id) references rc_quests_player_pools (id) on delete restrict on update restrict;

create index ix_rc_quests_player_pools_player_id on rc_quests_player_pools (player_id);
alter table rc_quests_player_pools add constraint fk_rc_quests_player_pools_player_id foreign key (player_id) references rc_quests_players (id) on delete restrict on update restrict;

create index ix_rc_quests_player_tasks_objective_id on rc_quests_player_tasks (objective_id);
alter table rc_quests_player_tasks add constraint fk_rc_quests_player_tasks_objective_id foreign key (objective_id) references rc_quests_player_objectives (id) on delete restrict on update restrict;

