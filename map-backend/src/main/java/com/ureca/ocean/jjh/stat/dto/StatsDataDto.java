package com.ureca.ocean.jjh.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDataDto {

	private StatPair usage;
	private StatPair savings;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StatPair {
		private int mine;
		private int average;
	}
}
